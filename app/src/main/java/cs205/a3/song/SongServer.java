package cs205.a3.song;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class SongServer {

    private static SongServer songServer;
    private final String server;

    private volatile List<SongReference> songs;

    private SongServer(String server) {
        this.server = server;
        this.songs = new ArrayList<>();
    }

    public List<SongReference> getSongs() {
        return songs;
    }

    public void setSongs(List<SongReference> songs) {
        this.songs = songs;
    }

    public static SongServer getInstance(String server) {
        if (songServer == null) {
            songServer = new SongServer(server);
        }

        return songServer;
    }
    public void startQuerySongs() {
        new Thread(() -> {
            try{
                URL oracle = new URL(server +"/api/collections/songs/records");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                songs.clear();
                while ((inputLine = in.readLine()) != null) {
                    songs.addAll(JsonUtils.getSongReferences(inputLine));
                }

                in.close();

            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }).start();
    }

    public void downloadSong(String id, String data, String audio, String server, File dst) {
        try{
            Thread dataDownload = new Thread(() -> {
                try{
                    String url = server + "/api/files/songs/" + id + "/" + data;
                    ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());

                    FileOutputStream fileOutputStream = new FileOutputStream(dst.getAbsolutePath()+ "/songData/"+id+".osu");
                    fileOutputStream.getChannel()
                            .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread audioDownload = new Thread(() -> {
                try{
                    String url = server + "/api/files/songs/" + id + "/" + audio;
                    ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());

                    FileOutputStream fileOutputStream = new FileOutputStream(dst.getAbsolutePath()+ "/songData/"+id+".mp3");
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
}
