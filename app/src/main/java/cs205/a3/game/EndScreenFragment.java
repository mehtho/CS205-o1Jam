package cs205.a3.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cs205.a3.R;
import cs205.a3.databinding.EndScreenFragmentBinding;
import cs205.a3.menus.MainActivity;
import cs205.a3.song.SongServer;
import cs205.a3.util.LeaderboardUtils;
import cs205.a3.util.NotificationPublisher;

/**
 * Class for the end screen fragment
 */
public class EndScreenFragment extends Fragment {

    private EndScreenFragmentBinding binding;

    /**
     * Displays the score achieved after the previous game
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = EndScreenFragmentBinding.inflate(inflater, container, false);
        binding.textviewScore.setText("" + getActivity().getIntent().getLongExtra("score", 0));
        binding.textviewSongName.setText(getActivity().getIntent()
                .getStringExtra("songName"));
        return binding.getRoot();

    }

    /**
     * Submits the score to the server after going back to the menu. Will send a notification if
     * the score is a high score on the leaderboard
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @SuppressLint("DefaultLocale")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 -> {
            SongServer songServer = SongServer.getInstance(getContext().getString(R.string.server));
            Intent intent = getActivity().getIntent();

            int place = songServer.submitScore(intent.getStringExtra("songId"),
                    LeaderboardUtils.readNameFile(getContext()),
                    intent.getLongExtra("score", 0));

            if (place > 0) {
                NotificationPublisher.showNotification(getContext(),
                        String.format("Congrats, you placed #%d on %s", place,
                                intent.getStringExtra("songName")));
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