package model;

import java.util.List;
import java.util.Map;

public class ScheduleResult {

    private List<GanttBlock> ganttBlocks;

    private Map<String, Integer> waitingTimes;
    private Map<String, Integer> turnaroundTimes;
    private Map<String, Integer> responseTimes;
    private Map<String, Integer> completionTimes;

    private double avgWaitingTime;
    private double avgTurnaroundTime;
    private double avgResponseTime;

    public ScheduleResult(
            List<GanttBlock> ganttBlocks,
            Map<String, Integer> waitingTimes,
            Map<String, Integer> turnaroundTimes,
            Map<String, Integer> responseTimes,
            Map<String, Integer> completionTimes,
            double avgWaitingTime,
            double avgTurnaroundTime,
            double avgResponseTime
    ) {
        this.ganttBlocks = ganttBlocks;
        this.waitingTimes = waitingTimes;
        this.turnaroundTimes = turnaroundTimes;
        this.responseTimes = responseTimes;
        this.completionTimes = completionTimes;
        this.avgWaitingTime = avgWaitingTime;
        this.avgTurnaroundTime = avgTurnaroundTime;
        this.avgResponseTime = avgResponseTime;
    }

    public List<GanttBlock> getGanttBlocks() {
        return ganttBlocks;
    }

    public Map<String, Integer> getWaitingTimes() {
        return waitingTimes;
    }

    public Map<String, Integer> getTurnaroundTimes() {
        return turnaroundTimes;
    }

    public Map<String, Integer> getResponseTimes() {
        return responseTimes;
    }

    public Map<String, Integer> getCompletionTimes() {
        return completionTimes;
    }

    public double getAvgWaitingTime() {
        return avgWaitingTime;
    }

    public double getAvgTurnaroundTime() {
        return avgTurnaroundTime;
    }

    public double getAvgResponseTime() {
        return avgResponseTime;
    }
}