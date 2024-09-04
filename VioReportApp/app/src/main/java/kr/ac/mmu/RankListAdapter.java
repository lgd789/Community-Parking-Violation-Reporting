package kr.ac.mmu;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankListAdapter extends RecyclerView.Adapter<RankListAdapter.ViewHolder> {
    private List<Rank> mRanks;

    public RankListAdapter(List<Rank> ranks) {
        mRanks = ranks;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rank rank = mRanks.get(position);

        if(rank.getRankNum() == 1) {holder.imageViewRank.setImageResource(R.drawable.rank_gold);}
        if(rank.getRankNum() == 2) {holder.imageViewRank.setImageResource(R.drawable.rank_silver);}
        if(rank.getRankNum() == 3) {holder.imageViewRank.setImageResource(R.drawable.rank_bronze);}
        if(rank.getRankNum() > 3){
            holder.textRank.setText(String.valueOf(rank.getRankNum()));
        }

        holder.textId.setText(rank.getId());
        holder.textPoint.setText(rank.getPoint());
    }

    @Override
    public int getItemCount() {
        return mRanks.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textRank, textId, textPoint;
        public ImageView imageViewRank;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewRank = itemView.findViewById(R.id.imageView_rank);
            textRank = itemView.findViewById(R.id.text_rank);
            textId = itemView.findViewById(R.id.text_id);
            textPoint = itemView.findViewById(R.id.text_point);
        }
    }
}
