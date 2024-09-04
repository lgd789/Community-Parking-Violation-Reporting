package kr.ac.mmu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReportRinfoAdapter extends RecyclerView.Adapter<ReportRinfoAdapter.ViewHolder> {
    private List<Picture> mPictures;
    private String id;
    private OnItemClickListener itemClickListener;
    public ReportRinfoAdapter(List<Picture> Pictures, String id) {
        mPictures = Pictures;
        this.id = id;

        Log.e("aaaa", id);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picture picture = mPictures.get(position);

        holder.reporter.setText(picture.getReporter());
        holder.reporter.bringToFront();
        String imagePath = picture.getReportImagePath();
        Glide.with(holder.itemView.getContext())
                .load("http://218.157.77.240:51212//app//uploads//" + imagePath)
                .into(holder.reportPicture);
        holder.reportCount.setText(picture.getReportCount() + "차");
        holder.reportId.setText("신고번호 : " + picture.getReportId());
        holder.reportTime.setText(picture.getReportTime());

        if (id.equals(picture.getReporter())) {
            holder.buttonDel.setVisibility(View.VISIBLE);
            // 기존에 설정된 OnClickListener를 제거하고 새로운 OnClickListener를 설정해야 합니다.
            holder.buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(picture.getReportId());
                    }
                }
            });
        } else {
            holder.buttonDel.setVisibility(View.INVISIBLE);
            holder.buttonDel.setOnClickListener(null);
        }

    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView reportPicture;
        public TextView reportCount;

        public TextView reportId;
        public TextView reporter;
        public TextView reportTime;
        public Button buttonDel;
        public ViewHolder(View itemView) {
            super(itemView);

            reporter = itemView.findViewById(R.id.textView_info_reporter);
            reportPicture = itemView.findViewById(R.id.imageView_info_image);
            reportCount = itemView.findViewById(R.id.textView_info_count);
            reportId = itemView.findViewById(R.id.textView_info_rid);
            reportTime = itemView.findViewById(R.id.textView_info_time);
            buttonDel = itemView.findViewById(R.id.button_delete);


        }
    }
    public interface OnItemClickListener {
        void onItemClick(String reportId);
    }
}