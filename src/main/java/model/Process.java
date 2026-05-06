package model;

public class Process {

    private int pid;
    private int arrivalTime;
    private int brustTime;



    private int remainigBurstTime; //Added this to stop when = 0
    private int priority;

    private int waitingTime; //Waiting Time = finishTime - ArrivalTime - BurstTime
    private int turnaroundTime;//doneRR
    private int responseTime; //doneRR
    private int finishTime; //I added this so I can calculate turnaround time and waiting time easily


    public Process(int pid, int arrivalTime, int brustTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.brustTime = brustTime;
        this.priority = priority;
    }

    public int getPid() {
        return pid;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setRemainigBurstTime(int remainigBurstTime) {
        this.remainigBurstTime = remainigBurstTime;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRemainigBurstTime() {
        return remainigBurstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getResponseTime() {
        return responseTime;
    }
    public int getFinishTime() {
        return finishTime;
    }

    public int getBrustTime() {
        return brustTime;
    }

    public void setBrustTime(int brustTime) {
        this.brustTime = brustTime;
    }
    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public void setFinishTime(int finishTime) { this.finishTime = finishTime; }
}