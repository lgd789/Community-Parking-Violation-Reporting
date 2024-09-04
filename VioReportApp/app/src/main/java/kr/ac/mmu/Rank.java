package kr.ac.mmu;

public class Rank {
    private static int count = 0;
    private int rankNum;
    private String id;
    private String point;

    public Rank(String id, String point) {
        this.count++;
        this.rankNum = count;
        this.id = id;
        this.point = point;
    }
    public static void setCount(int i){
        count = i;
    }
    public int getRankNum() { return rankNum; }
    public String getId() { return id; }
    public String getPoint() {
        return point;
    }
}
