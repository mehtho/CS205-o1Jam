package cs205.a3.menus;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import cs205.a3.R;
import cs205.a3.game.LeaderboardUtils;
import cs205.a3.song.SongReference;
import cs205.a3.song.SongServer;

/**
 * A fragment representing a list of Items.
 */
public class SongListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private List<SongReference> songReferenceList = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongListFragment() {
    }

    /**
     * Reads songs fetched from the server asynchronously and displays them
     *
     * Prompts for the user's name if necessary
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        songReferenceList = SongServer.getInstance(getString(R.string.server)).getSongs();
        if (LeaderboardUtils.readNameFile(getContext()) == null) {
            namePopUp();
        }
    }

    /**
     * Create the list view for the song list.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_list_fragment, container, false);
        View list = view.findViewById(R.id.list);

        if (list instanceof RecyclerView) {
            Context context = list.getContext();
            RecyclerView recyclerView = (RecyclerView) list;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(new SongListRecyclerViewAdapter(songReferenceList));
        }
        return view;
    }

    /**
     * Launches a name popup to prompt the user for their name if necessary
     */
    public void namePopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter your name!");

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.name_input, (ViewGroup) getView(), false);

        final TextInputEditText input = viewInflated.findViewById(R.id.input);

        builder.setView(viewInflated);

        //Write the username if given
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();

            new Thread(() -> LeaderboardUtils.writeToFile(input.getText().toString(),
                    getContext())).start();
        });

        //Re-prompt if cancelled
        builder.setOnCancelListener((onCancelListener)->{
            if (LeaderboardUtils.readNameFile(getContext()) == null) {
                namePopUp();
            }
        });

        builder.show();
    }
}