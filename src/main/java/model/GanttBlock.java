package model;

public class GanttBlock {

    private String pid;
    private int startTime;
    private int endTime;

    public GanttBlock(String pid, int startTime, int endTime) {
        this.pid = pid;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getPid() {
        return pid;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return "[" + pid + ": " + startTime + " → " + endTime + "]";
    }
}