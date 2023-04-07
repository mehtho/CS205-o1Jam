package cs205.a3.menus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import cs205.a3.R;
import cs205.a3.song.SongReference;
import cs205.a3.song.SongServer;

public class MainActivity extends FragmentActivity {
    private Future<List<SongReference>> queriedSongs;

    /**
     * Starts the main activity, which initialises the application.
     * <p>
     * Initialises the song list.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SongServer server = SongServer.getInstance(getString(R.string.server));
        queriedSongs = server.querySongs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startSongListDisplay();
    }

    /**
     * Plays a loading screen while songs load
     */
    @SuppressLint("SetTextI18n")
    private void startSongListDisplay() {
        new Thread(() -> {
            int dots = 1;
            while (!queriedSongs.isDone()) {
                TextView textView = findViewById(R.id.loading_text);
                textView.setText("Loading" + String.join("",
                        Collections.nCopies(dots, ".")));
                dots = ++dots % 4;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            runOnUiThread(() -> {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                //Removes loading screen here
                transaction.replace(R.id.song_list_placeholder, new SongListFragment());
                transaction.commit();
            });
        }).start();
    }
}