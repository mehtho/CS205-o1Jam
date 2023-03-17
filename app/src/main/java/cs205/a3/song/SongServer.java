package cs205.a3.song;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class SongServer {

    private static SongServer songServer;
    private final String server;

    private SongServer(String server) {
        this.server = server;
    }

    public static SongServer getInstance(String server) {
        if (songServer == null) {
            songServer = new SongServer(server);
        }

        return songServer;
    }
    public void getAllSongs() {
        GetAllSongsTask songsTask = new GetAllSongsTask();
        songsTask.execute(server);
    }


    private static class GetAllSongsTask extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            try{
                URL oracle = new URL(params[0] +"/api/collections/songs/records");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    System.out.println(inputLine);
                in.close();
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
            return resp;
        }
    }
}
