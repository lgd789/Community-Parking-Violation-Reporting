package kr.ac.mmu;

public class MyColor {
    private static String reportColor1;
    private static String reportColor2;
    private static String reportColor3;

    public MyColor(String reportColor1, String reportColor2, String reportColor3) {
        this.reportColor1 = reportColor1;
        this.reportColor2 = reportColor2;
        this.reportColor3 = reportColor3;
    }

    public static String getReportColor1() {
        return reportColor1;
    }

    public static String getReportColor2() {
        return reportColor2;
    }

    public static String getReportColor3() {
        return reportColor3;
    }
}
