package cs205.a3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import cs205.a3.databinding.GameFullscreenBinding;

public class GameActivity extends Activity {
    private GameFullscreenBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setContentView(R.layout.game_fullscreen);
        setContentView(binding.getRoot());
    }


}
