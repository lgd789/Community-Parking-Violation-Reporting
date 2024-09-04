package kr.ac.mmu;

public class Cmt {
    private String id;
    private String str;
    private String time;

    public Cmt(String id, String str, String time) {
        this.id = id;
        this.str = str;
        this.time = time;
    }

    public String getid() {
        return id;
    }

    public String getstr() {
        return str;
    }

    public String gettime() {
        return time;
    }

}