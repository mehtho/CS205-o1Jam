package cs205.a3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import cs205.a3.databinding.GameFullscreenBinding;

public class GameActivity extends Activity {
    private GameFullscreenBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading);
        loadFromServer();
    }

    public void loadFromServer() {
        Thread downloader = new Thread(() -> {
            // TODO: Actually downloading call here!
            int x = Integer.MAX_VALUE;
            while(x-->0){}

            doSetup();
        });
        downloader.start();
    }

    public void doSetup() {
        binding = GameFullscreenBinding.inflate(getLayoutInflater());
        Game game = Game.game;
        game.setSongName(getIntent().getStringExtra("songName"));

        binding.button1.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                game.tapLane(0);
                return true;
            }

            return false;
        });

        binding.button2.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                game.tapLane(1);
                return true;
            }

            return false;
        });

        binding.button3.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                game.tapLane(2);
                return true;
            }

            return false;
        });

        binding.button4.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                game.tapLane(3);
                return true;
            }
            return false;
        });
        runOnUiThread(() -> setContentView(binding.getRoot()));
    }
}
