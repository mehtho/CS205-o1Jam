package cs205.a3.song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private JsonUtils() {

    }

    public static List<SongReference> getSongReferences(String json) {
        List<SongReference> songs = new ArrayList<>();
        try{
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray =  jObject.getJSONArray("items");
            for (int i=0; i < jArray.length(); i++)
            {
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
}
