package kr.ac.mmu;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ReportMapInfoAdapter extends RecyclerView.Adapter<ReportMapInfoAdapter.ViewHolder> {
    private List<Picture> mPictures;

    public ReportMapInfoAdapter(List<Picture> Pictures) {
        mPictures = Pictures;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_info_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picture picture = mPictures.get(position);
        String imagePath = picture.getReportImagePath();
        Glide.with(holder.itemView.getContext())
                .load("http://218.157.77.240:51212//app//uploads//" + imagePath)
                .into(holder.reportPicture);
        if(getItemCount() == 1){
            ViewGroup.LayoutParams d = holder.linearLayout.getLayoutParams();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, d.height);
            params.setMargins(100, 0, 100, 0);
            holder.linearLayout.setLayoutParams(params);
        }
        else if(getItemCount() < 2){
            holder.reportCount.setText("2차 신고 요망");
            holder.reportCount.setTextColor(Color.parseColor("#FF0000"));
        }
        else {
            holder.reportCount.setText(Integer.toString(getItemCount()) + "차 신고");
            holder.reportCount.setTextColor(Color.parseColor("#00FF00"));
        }

        holder.reportCount.setText(picture.getReportCount() + "차 신고");
        holder.reportId.setText("신고번호 : " + picture.getReportId());
        holder.reporter.setText("신고자 : " + picture.getReporter());
        holder.reportTime.setText("신고시간 : " + picture.getReportTime());
    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public ImageView reportPicture;
        public TextView reportCount;

        public TextView reportId;
        public TextView reporter;
        public TextView reportTime;
        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearlayout);
            reportPicture = itemView.findViewById(R.id.imageView_map_info_image);
            reportCount = itemView.findViewById(R.id.textView_map_info_count);
            reportId = itemView.findViewById(R.id.textView_map_info_rid);
            reporter = itemView.findViewById(R.id.textView_map_info_reporter);
            reportTime = itemView.findViewById(R.id.textView_map_info_time);
        }
    }
}