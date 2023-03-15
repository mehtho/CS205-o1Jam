package cs205.a3;

import android.annotation.SuppressLint;
import android.app.Activity;
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
        System.out.println("YES");
        binding = GameFullscreenBinding.inflate(getLayoutInflater());
        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            System.out.println("TOUCHED");
            return true;
        });

        binding.button1.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){

                System.out.println("B1");
                return true;
            }
            return false;
        });

        binding.button2.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){

                System.out.println("B2");
                return true;
            }
            return false;
        });

        binding.button3.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){

                System.out.println("B3");
                return true;
            }
            return false;
        });

        binding.button4.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){

                System.out.println("B4");
                return true;
            }
            return false;
        });

        setContentView(R.layout.game_fullscreen);
        View view = binding.getRoot();
        setContentView(view);
    }


}
