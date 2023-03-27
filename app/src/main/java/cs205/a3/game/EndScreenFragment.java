package cs205.a3.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cs205.a3.MainActivity;
import cs205.a3.NotificationPublisher;
import cs205.a3.R;
import cs205.a3.databinding.FragmentFirstBinding;
import cs205.a3.song.SongServer;

public class EndScreenFragment extends Fragment {

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

    @SuppressLint("DefaultLocale")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 -> {
            SongServer songServer = SongServer.getInstance(getContext().getString(R.string.server));
            int place = songServer.submitScore(getActivity().getIntent()
                            .getStringExtra("songId"), LeaderboardUtils.readNameFile(getContext()),
                    getActivity().getIntent().getLongExtra("score", 0));
            if (place > 0) {
                NotificationPublisher.showNotification(getContext(),
                        String.format("Congrats, you placed #%d on %s", place, getActivity().getIntent()
                                .getStringExtra("songName")));
            }

            view1.getContext().startActivity(new Intent(view1.getContext(), MainActivity.class));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}