package cs205.a3;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cs205.a3.databinding.SongItemBinding;
import cs205.a3.placeholder.PlaceholderContent.PlaceholderItem;
import cs205.a3.song.SongReference;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SongListRecyclerViewAdapter extends RecyclerView.Adapter<SongListRecyclerViewAdapter.ViewHolder> {

    private final List<SongReference> mValues;

    public SongListRecyclerViewAdapter(List<SongReference> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(SongItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getName());
        holder.itemView.setOnClickListener(x -> {
            Intent intent = new Intent(holder.itemView.getContext(), GameActivity.class);
            intent.putExtra("songName", mValues.get(position).getName());
            intent.putExtra("songId", mValues.get(position).getId());
            intent.putExtra("songData", mValues.get(position).getData());
            intent.putExtra("songAudio", mValues.get(position).getAudio());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mContentView;
        public SongReference mItem;

        public ViewHolder(SongItemBinding binding) {
            super(binding.getRoot());
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}