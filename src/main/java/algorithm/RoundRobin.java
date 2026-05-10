package algorithm;

import java.util.*;
import model.GanttBlock;
import model.Process;
import model.ScheduleResult;

public class RoundRobin {
    //gantt block and idle 
    private Queue<Process> readyQRR = new LinkedList<>();
    ArrayList<GanttBlock> gantt = new ArrayList<>();

    private int RRCnt = 0;//start of the process



    ScheduleResult startRR(int quantum, ArrayList<Process> processes) {
        ArrayList<Process> allProcesses = new ArrayList<>(processes);
        initializeReadyQ(processes);

        while (!readyQRR.isEmpty() || !processes.isEmpty()) {
            if (readyQRR.isEmpty()) {

                if (!processes.isEmpty()) {
                    GanttBlock idle = new GanttBlock("IDLE", RRCnt, processes.get(0).getArrivalTime());
                    gantt.add(idle);
                    RRCnt = processes.get(0).getArrivalTime();
                    initializeReadyQ(processes);
                } else {
                    break;
                }

                continue;
            }
            if (RRCnt < readyQRR.peek().getArrivalTime()) {
                GanttBlock idle = new GanttBlock("IDLE", RRCnt, readyQRR.peek().getArrivalTime());
                gantt.add(idle);
                RRCnt = readyQRR.peek().getArrivalTime();
            }

            if (readyQRR.peek().getResponseTime() == -1) {
                readyQRR.peek().setResponseTime(RRCnt);
            }

            int remaining = readyQRR.peek().getRemainigBurstTime();
            if (remaining <= quantum) {
                readyQRR.peek().setRemainigBurstTime(0);
                String PID = "P" + readyQRR.peek().getPid();
                GanttBlock box = new GanttBlock(PID, RRCnt, RRCnt + remaining);
                gantt.add(box);
                RRCnt += remaining;
            } else {
                readyQRR.peek().setRemainigBurstTime(remaining - quantum);
                String PID = "P" + readyQRR.peek().getPid();
                GanttBlock box = new GanttBlock(PID, RRCnt, RRCnt + quantum);
                gantt.add(box);
                RRCnt += quantum;
            }

            if (readyQRR.peek().getRemainigBurstTime() == 0) {
                readyQRR.peek().setFinishTime(RRCnt);
                readyQRR.peek().setTurnaroundTime(
                        readyQRR.peek().getFinishTime() - readyQRR.peek().getArrivalTime());
                readyQRR.peek().setWaitingTime(
                        readyQRR.peek().getFinishTime() - readyQRR.peek().getArrivalTime()
                                - readyQRR.peek().getBrustTime());
                readyQRR.poll();
                initializeReadyQ(processes);
            } else {
                readyQRR.add(readyQRR.peek());
                readyQRR.poll();
                initializeReadyQ(processes);
            }
        }

        ScheduleResult resultRR = calcAVGRR(allProcesses);
        return resultRR;
    }
    void initializeReadyQ(ArrayList<Process> processes) {
        Iterator<Process> iterator = processes.iterator();
        while (iterator.hasNext()) {
            Process p = iterator.next();
            if (p.getArrivalTime() <= RRCnt) {
                readyQRR.add(p);
                iterator.remove();
            }
        }
    }

    void printRQRR() {
        System.out.println("Current Ready Queue: "+ readyQRR);
    }

    ScheduleResult calcAVGRR(ArrayList<Process> allprocesses){
        double sumTAT = 0;
        double sumRT = 0;
        double sumWT = 0;
        int n = allprocesses.size();
        for (int i = 0 ; i < n ; i++){
            sumTAT += allprocesses.get(i).getTurnaroundTime();
            sumRT += allprocesses.get(i).getResponseTime();
            sumWT += allprocesses.get(i).getWaitingTime();
        }
        return new ScheduleResult(
                gantt, allprocesses, sumWT / n, sumTAT / n, sumRT / n);
    }

}