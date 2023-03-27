package cs205.a3.song;

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

import cs205.a3.NIOThreadPool;
import cs205.a3.scorecalc.Score;

public class SongServer {
    private static SongServer songServer;
    private final Object songMutex = new Object();
    private final String server;

    private final List<SongReference> songs;

    private final NIOThreadPool nioThreadPool = new NIOThreadPool();

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

    public void startQuerySongs() {
        try {
            List<SongReference> newSongs = querySongs().get();
            synchronized (songMutex) {
                songs.clear();
                songs.addAll(newSongs);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private Future<List<SongReference>> querySongs() {
        return nioThreadPool.submitSongCall(() -> {
            ArrayList<SongReference> newSongs = new ArrayList<>();
            try {
                URL oracle = new URL(server + "/api/collections/songs/records");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    newSongs.addAll(JsonUtils.getSongReferences(inputLine));
                }
                in.close();
                return newSongs;
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
            return newSongs;
        });
    }

    public int submitScore(String songId, String username, long score) {
        try {
            List<Score> scores = getScoresForSongAndUser(songId, username).get();

            for (Score sc : scores) {
                if (sc.getScore() < score) {
                    updateScoreSubmission(new Score(sc.getId(), score, username, songId));
                    return topTenCheck(songId, username);
                }
                return 0;
            }

            sendScoreSubmission(songId, username, score);
            return topTenCheck(songId, username);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int topTenCheck(String songId, String username) throws InterruptedException, ExecutionException {
        List<Score> topTen = getScoresForSong(songId).get();
        for (int i = 0; i < topTen.size(); i++) {
            if (topTen.get(i).getName().equals(username)) {
                return i + 1;
            }
        }
        return 0;
    }

    private void updateScoreSubmission(Score score) {
        new Thread(() -> {
            try {
                URL url = new URL(server + "/api/collections/scores/records/" + score.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PATCH");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("song", score.getSongId());
                jsonParam.put("name", score.getName());
                jsonParam.put("score", score.getScore());

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                conn.getResponseMessage();
                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendScoreSubmission(String songId, String username, long score) {
        new Thread(() -> {
            try {
                URL url = new URL(server + "/api/collections/scores/records");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("song", songId);
                jsonParam.put("name", username);
                jsonParam.put("score", score);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                conn.getResponseMessage();
                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void downloadSong(String id, String data, String audio, String server, File dst) {
        try {
            File directory = new File(dst.getAbsolutePath() + "/songData");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            Thread dataDownload = new Thread(() -> {
                try {
                    String url = server + "/api/files/songs/" + id + "/" + data;
                    ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());

                    FileOutputStream fileOutputStream = new FileOutputStream(dst.getAbsolutePath() + "/songData/" + id + ".osu");
                    fileOutputStream.getChannel()
                            .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread audioDownload = new Thread(() -> {
                try {
                    String url = server + "/api/files/songs/" + id + "/" + audio;
                    ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());

                    FileOutputStream fileOutputStream = new FileOutputStream(dst.getAbsolutePath() + "/songData/" + id + ".mp3");
                    fileOutputStream.getChannel()
                            .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            dataDownload.start();
            audioDownload.start();
            dataDownload.join();
            audioDownload.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Future<List<Score>> getScoresForSong(String songId) {
        return nioThreadPool.submitScoreCall(() -> {
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

                return scores;
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
            return new ArrayList<>();
        });
    }

    private Future<List<Score>> getScoresForSongAndUser(String songId, String username) {
        return nioThreadPool.submitScoreCall(() -> {
            try {
                URL url = new URL(server + "/api/collections/scores/records?filter="
                        + URLEncoder.encode(String.format("(song=\"%s\"&&name=\"%s\")", songId, username), StandardCharsets.UTF_8.toString()));
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
