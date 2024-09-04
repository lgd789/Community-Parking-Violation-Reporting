package kr.ac.mmu;

public class Report {
    private String reportNumber;
    private String address;
    private String carNumber;
    private String reportCount;
    private String latitude;
    private String longitude;

    public Report(String reportNumber, String address, String licensePlateNumber, String reportCount, String latitude, String longitude) {
        this.reportNumber = reportNumber;
        this.address = address;
        this.carNumber = licensePlateNumber;
        this.reportCount = String.valueOf((Integer.valueOf(reportCount) + 1));
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getReportNumber() {
        return reportNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getLicensePlateNumber() {
        return carNumber;
    }

    public String getReportCount() {
        return reportCount;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getLongitude() {
        return longitude;
    }

}