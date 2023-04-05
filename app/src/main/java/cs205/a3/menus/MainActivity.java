package cs205.a3.menus;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import cs205.a3.R;
import cs205.a3.song.SongServer;

public class MainActivity extends FragmentActivity {
    /**
     * Starts the main activity, which initialises the application.
     *
     * Initialises the song list.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SongServer server = SongServer.getInstance(getString(R.string.server));
        server.startQuerySongs();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.song_list_placeholder, new SongListFragment());
        transaction.commit();
    }
}