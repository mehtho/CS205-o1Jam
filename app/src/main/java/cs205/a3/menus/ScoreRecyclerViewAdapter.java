package cs205.a3.menus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cs205.a3.databinding.ScoreFragmentItemBinding;
import cs205.a3.scorecalc.Score;
import cs205.a3.util.LeaderboardUtils;

/**
 * Displays a list view for a list of scores on the leaderboard
 */
public class ScoreRecyclerViewAdapter extends RecyclerView.Adapter<ScoreRecyclerViewAdapter.ViewHolder> {

    private final List<Score> mValues;
    private final String songName;

    public ScoreRecyclerViewAdapter(List<Score> items, String songName) {
        mValues = items;
        this.songName = songName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ScoreFragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("" + mValues.get(position).getScore());
        holder.mContentView.setText(mValues.get(position).getName());

        //Sets the action to send an email if the user owns the high score
        String me = LeaderboardUtils.readNameFile(holder.mButton.getContext()).trim();

        if (me.equals(mValues.get(position).getName().trim())) {
            holder.mButton.setVisibility(View.VISIBLE);
            holder.mButton.setOnClickListener(x -> {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "o1Jam high score");
                i.putExtra(Intent.EXTRA_TEXT, String.format("I scored %d on %s! Beat that!",
                        mValues.get(position).getScore(), this.songName));

                try {
                    x.getContext().startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(x.getContext(), "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.mButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageButton mButton;
        public Score mItem;

        public ViewHolder(ScoreFragmentItemBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            mButton = binding.shareButton;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}