package cs205.a3.song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cs205.a3.scorecalc.Score;

public class JsonUtils {
    private JsonUtils() {

    }

    public static List<SongReference> getSongReferences(String json) {
        List<SongReference> songs = new ArrayList<>();
        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("items");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo = jArray.getJSONObject(i);
                songs.add(new SongReference(
                        jo.getString("song_name"),
                        jo.getString("id"),
                        jo.getString("song_data"),
                        jo.getString("song_audio")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public static List<Score> getScoreList(String json) {
        List<Score> scores = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("items");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo = jArray.getJSONObject(i);
                scores.add(new Score(
                        jo.getString("id"),
                        jo.getLong("score"),
                        jo.getString("name"),
                        jo.getString("song")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return scores;
    }

    public static String getScoreSubmission(String songId, String username, long score) {
        return String.format("{'song': '%s','name': '%s', 'score': %d}", songId, username, score);
    }
}
