package cs205.a3.song;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cs205.a3.scorecalc.Score;
import cs205.a3.util.JsonUtils;
import cs205.a3.util.NotificationPublisher;

/**
 * Class to handle communications between the application and the server
 */
public class SongServer {
    private static SongServer songServer;
    private final Object songMutex = new Object();
    private final String server;

    // List of song references to display
    private final List<SongReference> songs;

    // Thread pool for network requests
    private final NetworkThreadPool networkThreadPool = new NetworkThreadPool();

    private SongServer(String server) {
        this.server = server;
        this.songs = new ArrayList<>();
    }

    public static SongServer getInstance(String server) {
        if (songServer == null) {
            songServer = new SongServer(server);
        }

        return songServer;
    }

    public List<SongReference> getSongs() {
        synchronized (songMutex) {
            return songs;
        }
    }

    /**
     * Queries songs from the server
     *
     * @return Future object with a list of available songs
     */
    public Future<List<SongReference>> querySongs() {
        return networkThreadPool.submitSongCall(() -> {
            ArrayList<SongReference> newSongs = new ArrayList<>();
            while (newSongs.isEmpty()) {
                try {
                    URL oracle = new URL(server + "/api/collections/songs/records");
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        newSongs.addAll(JsonUtils.getSongReferences(inputLine));
                    }
                    in.close();
                } catch (IOException e) {

                }
            }
            synchronized (songMutex) {
                songs.clear();
                songs.addAll(newSongs);
            }

            return newSongs;
        });
    }

    /**
     * Submits a score to the server
     *
     * @param songId   ID of the song for the score
     * @param username User's username
     * @param score    User's score
     * @return Leaderboard position
     */
    public void submitScore(String songId, String username, long score, Context context,
                            String songName) {
        networkThreadPool.submitTask(() ->{
            int place = 0;
            try {
                List<Score> scores = getScoresForSongAndUser(songId, username).get();
                boolean found = false;

                for (Score sc : scores) {
                    if (sc.getScore() < score) {
                        updateScoreSubmission(new Score(sc.getId(), score, username, songId));
                        place = topTenCheck(songId, username);
                        found = true;
                    }
                }
                if(!found) {
                    sendScoreSubmission(songId, username, score);
                    place = topTenCheck(songId, username);
                }
            } catch (ExecutionException | InterruptedException e) {
                place = 0;
            }

            if(place != 0){
                NotificationPublisher.showNotification(context,
                        String.format("Congrats, you placed #%d on %s", place,
                                songName));
            }
        });
    }

    /**
     * Checks if the user's score is in the top 10
     *
     * @param songId   ID of the song to check
     * @param username Username to check for
     * @return Leaderboard position if any
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private int topTenCheck(String songId, String username)
            throws InterruptedException, ExecutionException {
        Thread.sleep(2000);
        List<Score> topTen = getScoresForSong(songId).get();
        for (int i = 0; i < topTen.size(); i++) {
            if (topTen.get(i).getName().equals(username)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Updates a score if it is higher
     *
     * @param score Score object to update to
     */
    private void updateScoreSubmission(Score score) {
        networkThreadPool.submitTask(() -> {
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("song", score.getSongId());
                jsonParam.put("name", score.getName());
                jsonParam.put("score", score.getScore());
                sendRequest(new URL(server + "/api/collections/scores/records/" + score.getId()), "PATCH", jsonParam);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Submit a new score
     *
     * @param songId   Song ID to submit for
     * @param username User's username
     * @param score    Score to submit
     */
    private void sendScoreSubmission(String songId, String username, long score) {
        networkThreadPool.submitTask(() -> {
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("song", songId);
                jsonParam.put("name", username);
                jsonParam.put("score", score);

                sendRequest(new URL(server + "/api/collections/scores/records"),
                        "POST", jsonParam);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sends a request to the requested URL
     *
     * @param url        Requested URL
     * @param method     HTTP Method
     * @param jsonObject JSON body
     * @throws IOException
     */
    private void sendRequest(URL url, String method, JSONObject jsonObject) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
        os.writeBytes(jsonObject.toString());

        os.flush();
        os.close();

        conn.getResponseMessage();
        conn.disconnect();
    }

    /**
     * Download the song's audio and game file
     *
     * @param id     ID of the song's data to download
     * @param data   Song data file name
     * @param audio  Song audio file name
     * @param server Servel URL
     * @param dst    File to write to
     */
    public void downloadSong(String id, String data, String audio, String server, File dst) {
        File directory = new File(dst.getAbsolutePath() + "/songData");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        //Song data downloading thread
        Future<?> dataDownload = networkThreadPool.submitTask(() -> {
            boolean success = false;
            while (!success) {
                try {
                    String url = server + "/api/files/songs/" + id + "/" + data;
                    ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());

                    FileOutputStream fileOutputStream = new FileOutputStream(dst.getAbsolutePath() + "/songData/" + id + ".osu");
                    fileOutputStream.getChannel()
                            .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    success = true;
                } catch (IOException e) {
                    //Do nothing and retry
                }
            }
        });

        //Song audio downloading thread
        Future<?> audioDownload = networkThreadPool.submitTask(() -> {
            boolean success = false;
            while (!success) {
                try {
                    String url = server + "/api/files/songs/" + id + "/" + audio;
                    ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());

                    FileOutputStream fileOutputStream = new FileOutputStream(dst.getAbsolutePath() + "/songData/" + id + ".mp3");
                    fileOutputStream.getChannel()
                            .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    success = true;
                } catch (IOException e) {
                    //Do nothing and retry
                }
            }
        });

        //Wait until both downloads are complete
        while (!dataDownload.isDone() || !audioDownload.isDone()) ;
    }

    /**
     * Gets the submitted scores for a given song
     *
     * @param songId Song ID to get scores for
     * @return Song scores
     */
    public Future<List<Score>> getScoresForSong(String songId) {
        return networkThreadPool.submitScoreCall(() -> {
            boolean success = false;
            while (!success) {
                try {
                    URL oracle = new URL(server
                            + String.format("/api/collections/scores/records?filter=(song='%s')&sort=-score&perPage=10", songId));
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    List<Score> scores = new ArrayList<>();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        scores.addAll(JsonUtils.getScoreList(inputLine));
                    }

                    in.close();
                    success = true;

                    return scores;
                } catch (IOException e) {
                    //Do nothing
                }
            }
            return new ArrayList<>();
        });
    }

    /**
     * Gets scores for a user on a song
     *
     * @param songId   Song ID to get scores for
     * @param username Username to get scores for
     * @return
     */
    private Future<List<Score>> getScoresForSongAndUser(String songId, String username) {
        return networkThreadPool.submitScoreCall(() -> {
            try {
                URL url = new URL(server + "/api/collections/scores/records?filter="
                        + URLEncoder.encode(String.format(
                                "(song=\"%s\"&&name=\"%s\")",
                                songId, username),
                        StandardCharsets.UTF_8.toString()));

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));

                List<Score> scores = new ArrayList<>();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    scores.addAll(JsonUtils.getScoreList(inputLine));
                }

                in.close();

                return scores;
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
            return new ArrayList<>();
        });
    }
}
