package kr.ac.mmu;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.ac.mmu.R;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder> {
    private List<Report> mReports;
    private OnItemClickListener mListener = null;

    public ReportListAdapter(List<Report> reports) {
            mReports = reports;
    }

    public void setReports(List<Report> reports) {
        mReports = reports;
    }

    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = mReports.get(position);
        holder.reportNumber.setText(String.valueOf(report.getReportNumber()));
        holder.location.setText(report.getAddress());
        holder.licensePlateNumber.setText(report.getLicensePlateNumber());
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
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reportNumber;
        public TextView reporter;
        public TextView location;
        public TextView licensePlateNumber;
        public TextView reportCount;
        private ProgressBar progress;
        ProgressDialog dialog;

        public ViewHolder(View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.progressBar);
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
            reportNumber = itemView.findViewById(R.id.report_number);
            reporter = itemView.findViewById(R.id.reporter);
            location = itemView.findViewById(R.id.location);
            licensePlateNumber = itemView.findViewById(R.id.license_plate_number);
            reportCount = itemView.findViewById(R.id.report_count);
        }
    }
}