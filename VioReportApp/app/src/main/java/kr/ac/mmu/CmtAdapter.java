package kr.ac.mmu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CmtAdapter extends RecyclerView.Adapter<CmtAdapter.ViewHolder> {
    private List<Cmt> mCmt;
    public CmtAdapter(List<Cmt> cmts) {
        mCmt = cmts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cmt_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cmt cmt = mCmt.get(position);
        holder.cmtId.setText(cmt.getid());
        holder.cmtStr.setText(cmt.getstr());
        holder.cmtTime.setText(cmt.gettime());
    }

    @Override
    public int getItemCount() {
        return mCmt.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cmtId;
        public TextView cmtStr;
        public TextView cmtTime;
        public ViewHolder(View itemView) {
            super(itemView);

            cmtId = itemView.findViewById(R.id.cmt_id);
            cmtStr = itemView.findViewById(R.id.cmt_str);
            cmtTime = itemView.findViewById(R.id.cmt_time);
        }
    }
}