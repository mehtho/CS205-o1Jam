package cs205.a3.game;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;

import cs205.a3.R;
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