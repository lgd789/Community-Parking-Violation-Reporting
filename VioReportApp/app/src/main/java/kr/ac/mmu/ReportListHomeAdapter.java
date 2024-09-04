package kr.ac.mmu;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReportListHomeAdapter extends RecyclerView.Adapter<ReportListHomeAdapter.ViewHolder> {
    private List<Report> mReports;
    public ReportListHomeAdapter(List<Report> reports) {
        mReports = reports;
    }
    private OnItemClickListener mListener = null;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_home_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = mReports.get(position);

        holder.reportCount.setText(report.getReportCount() + "차신고 진행중");
        if (Integer.valueOf(report.getReportCount()) == 2) {
            holder.reportCount.setTextColor(Color.parseColor(MyColor.getReportColor1()));
        }
        else if (Integer.valueOf(report.getReportCount()) == 3) {
            holder.reportCount.setTextColor(Color.parseColor(MyColor.getReportColor2()));
        }
        else if (Integer.valueOf(report.getReportCount()) > 3) {
            holder.reportCount.setTextColor(Color.parseColor(MyColor.getReportColor3()));
        }
        holder.location.setText(report.getAddress());
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }


    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView location;
        public TextView reportCount;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        mListener.onItemClick(v, pos);
                    }

                }
            });

            reportCount = itemView.findViewById(R.id.report_count_home);
            location = itemView.findViewById(R.id.location);
        }
    }
}