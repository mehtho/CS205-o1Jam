package cs205.a3.game;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cs205.a3.databinding.ActivityEndScreenBinding;

public class EndScreen extends AppCompatActivity {
    private ActivityEndScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEndScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}