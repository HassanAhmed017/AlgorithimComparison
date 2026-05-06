 package model;

import java.util.List;

public class ScheduleResult {

    private List<GanttBlock> ganttBlocks;
    private List<Process> processes;

    private double avgWaitingTime;
    private double avgTurnaroundTime;
    private double avgResponseTime;

    public ScheduleResult(
            List<GanttBlock> ganttBlocks,
            List<Process> processes,
            double avgWaitingTime,
            double avgTurnaroundTime,
            double avgResponseTime
    ) {
        this.ganttBlocks = ganttBlocks;
        this.processes = processes;
        this.avgWaitingTime = avgWaitingTime;
        this.avgTurnaroundTime = avgTurnaroundTime;
        this.avgResponseTime = avgResponseTime;
    }

    public List<GanttBlock> getGanttBlocks() { return ganttBlocks; }
    public List<Process> getProcesses() { return processes; }

    public double getAvgWaitingTime() { return avgWaitingTime; }
    public double getAvgTurnaroundTime() { return avgTurnaroundTime; }
    public double getAvgResponseTime() { return avgResponseTime; }
}