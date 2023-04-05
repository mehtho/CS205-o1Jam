package cs205.a3.menus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Future;

import cs205.a3.R;
import cs205.a3.databinding.SongItemBinding;
import cs205.a3.game.GameActivity;
import cs205.a3.scorecalc.Score;
import cs205.a3.song.SongReference;
import cs205.a3.song.SongServer;

/**
 * Displays the list of songs in the song menu
 */
public class SongListRecyclerViewAdapter
        extends RecyclerView.Adapter<SongListRecyclerViewAdapter.ViewHolder> {

    private final List<SongReference> mValues;

    public SongListRecyclerViewAdapter(List<SongReference> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(SongItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getName());

        //Set each button to start the song
        holder.itemView.setOnClickListener(x -> {
            Intent intent = new Intent(holder.itemView.getContext(), GameActivity.class);
            intent.putExtra("songName", mValues.get(position).getName());
            intent.putExtra("songId", mValues.get(position).getId());
            intent.putExtra("songData", mValues.get(position).getData());
            intent.putExtra("songAudio", mValues.get(position).getAudio());
            holder.itemView.getContext().startActivity(intent);
        });

        //Set each button to navigate to the leaderboard
        holder.mButton.setOnClickListener(x -> {
            Bundle bundle = new Bundle();
            bundle.putString("songId", mValues.get(position).getId());
            bundle.putString("songName", mValues.get(position).getName());
            ScoreFragment fragInfo = new ScoreFragment(() ->
                    loadSongs(holder.mContentView.getContext(),
                            mValues.get(position).getId()));

            fragInfo.setArguments(bundle);

            FragmentActivity activity = (FragmentActivity) holder.itemView.getContext();

            FragmentTransaction transaction = activity
                    .getSupportFragmentManager()
                    .beginTransaction();

            transaction
                    .replace(R.id.song_list_placeholder, fragInfo)
                    .addToBackStack("leaderboard")
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private Future<List<Score>> loadSongs(Context context, String songId) {
        SongServer songServer = SongServer.getInstance(
                context.getString(R.string.server));
        return songServer.getScoresForSong(songId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mContentView;
        public final ImageButton mButton;
        public SongReference mItem;

        public ViewHolder(SongItemBinding binding) {
            super(binding.getRoot());
            mContentView = binding.content;
            mButton = binding.lbButton;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}