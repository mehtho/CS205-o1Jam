package cs205.a3.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cs205.a3.MainActivity;
import cs205.a3.R;
import cs205.a3.databinding.FragmentFirstBinding;
import cs205.a3.song.SongServer;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        binding.textviewScore.setText("" + getActivity().getIntent().getLongExtra("score", 0));
        binding.textviewSongName.setText(getActivity().getIntent()
                .getStringExtra("songName"));
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongServer songServer = SongServer.getInstance(getContext().getString(R.string.server));
                songServer.submitScore(getActivity().getIntent()
                                .getStringExtra("songId"), LeaderboardUtils.readNameFile(getContext()),
                        getActivity().getIntent().getLongExtra("score", 0));

                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}