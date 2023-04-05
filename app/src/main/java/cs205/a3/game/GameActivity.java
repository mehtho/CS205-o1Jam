package cs205.a3.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import java.io.File;

import cs205.a3.R;
import cs205.a3.databinding.GameFullscreenBinding;
import cs205.a3.song.SongServer;

/**
 * Activity that holds the game
 */
public class GameActivity extends Activity {
    private GameFullscreenBinding binding;

    /**
     * Displays a loading screen while loading song elements and downloading
     * the song if necessary
     * @param savedInstanceState
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading);
        loadFromServer();
    }

    /**
     * Loads the song from file if present, or fetches it from the server if necessary
     */
    public void loadFromServer() {
        String songId = getIntent().getStringExtra("songId");
        Thread downloader = new Thread(() -> {
            if (!new File(getFilesDir().getAbsolutePath() + "/songData/" + songId + ".mp3").exists()
                    || !new File(getFilesDir().getAbsolutePath() + "/songData/" + songId + ".osu").exists()
            ) {
                SongServer.getInstance(getString(R.string.server))
                        .downloadSong(songId, getIntent().getStringExtra("songData"),
                                getIntent().getStringExtra("songAudio"),
                                getString(R.string.server),
                                getFilesDir());
            }

            doSetup();
        });
        downloader.start();
    }

    /**
     * Setup procedure for the game activity.
     * This includes initialising UI elements, particularly the key to press by assigning them
     * actions to perform upon input.
     *
     * Given the time-sensitive nature of a rhythm game, inputs are taken once keys are touched,
     * instead of when touched then released.
     */
    @SuppressLint("ClickableViewAccessibility")
    public void doSetup() {
        binding = GameFullscreenBinding.inflate(getLayoutInflater());
        Game game = GameView.game;
        game.setSongPath(getFilesDir() + "/songData/");
        game.initSong(getIntent().getStringExtra("songId"),
                getIntent().getStringExtra("songName"));

        binding.button1.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                game.tapLane(0);
                return true;
            }

            return false;
        });

        binding.button2.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                game.tapLane(1);
                return true;
            }

            return false;
        });

        binding.button3.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                game.tapLane(2);
                return true;
            }

            return false;
        });

        binding.button4.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                game.tapLane(3);
                return true;
            }
            return false;
        });
        runOnUiThread(() -> setContentView(binding.getRoot()));
    }

    /**
     * Stops the game when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        GameView.game.stopRunning();
    }
}
