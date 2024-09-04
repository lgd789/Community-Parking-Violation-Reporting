package kr.ac.mmu;

public class MarkerData {
    String name;
    Double latitude;
    Double longitude;

    Integer reportCount;
    public MarkerData() {}
    public MarkerData(String reportId, String carNumber, Double latitude, Double longitude, Integer reportCount) {
        this.name = reportId + "/" + carNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportCount = reportCount + 1;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() { return longitude; }

    public Integer getReportCount() {
        return reportCount;
    }
}
