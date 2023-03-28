package cs205.a3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cs205.a3.scorecalc.Score;
import cs205.a3.song.SongServer;

/**
 * A fragment representing a list of Items.
 */
public class ScoreFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScoreFragment() {
    }

    @SuppressWarnings("unused")
    public static ScoreFragment newInstance(int columnCount) {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.score_fragment_item_list, container, false);
        View list = view.findViewById(R.id.list);

        ((TextView) view.findViewById(R.id.lb_song_name)).setText(this.getArguments().getString("songName"));

        if (list instanceof RecyclerView) {
            Context context = list.getContext();
            RecyclerView recyclerView = (RecyclerView) list;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(new ScoreRecyclerViewAdapter(loadScores(),
                    this.getArguments().getString("songName")));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.loading).setVisibility(View.GONE);
    }

    public List<Score> loadScores() {
        SongServer songServer = SongServer.getInstance(getString(R.string.server));
        Future<List<Score>> scoreFuture
                = songServer.getScoresForSong(this.getArguments().getString("songId"));
        try {
            List<Score> scores = scoreFuture.get();
            if (!scores.isEmpty()) {
                return scores;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ArrayList<Score> err = new ArrayList<>();
        err.add(new Score("", 0, "No Scores Yet!", "Id"));
        return err;
    }
}