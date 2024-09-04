package kr.ac.mmu;

import android.graphics.Bitmap;
import android.widget.TextView;

public class Picture {
    private String imagePath;
    public String reportId;
    public String reporter;
    public String reportTime;
    public int reportCount;
    public Picture(String imagePath, String reportId, String reporter, String reportTime, int reportCount) {
        this.imagePath = imagePath;
        this.reportId = reportId;
        this.reporter = reporter;
        this.reportTime = reportTime;
        this.reportCount = reportCount;
    }

    public String getReportImagePath() {
        return imagePath;
    }
    public String getReportId() {
        return reportId;
    }
    public String getReporter() {
        return reporter;
    }
    public String getReportTime() {
        return reportTime;
    }
    public int getReportCount() { return reportCount; }

}