package cs205.a3.song;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
    public void getAllSongs() {
        new Thread(() -> {
            try{
                URL oracle = new URL(server +"/api/collections/songs/records");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
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
}
