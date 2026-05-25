package algorithm;

import java.util.*;
import model.GanttBlock;
import model.Process;
import model.ScheduleResult;

public class RoundRobin {

    ScheduleResult startRR(int quantum, ArrayList<Process> processes) {
        Queue<Process> readyQRR = new LinkedList<>();
        ArrayList<GanttBlock> gantt = new ArrayList<>();
        int RRCnt = 0;

        ArrayList<Process> allProcesses = new ArrayList<>(processes);
        ArrayList<Process> remaining = new ArrayList<>(processes);

        while (!readyQRR.isEmpty() || !remaining.isEmpty()) {
            if (readyQRR.isEmpty()) {
                if (!remaining.isEmpty()) {
                    GanttBlock idle = new GanttBlock("IDLE", RRCnt, remaining.get(0).getArrivalTime());
                    gantt.add(idle);
                    RRCnt = remaining.get(0).getArrivalTime();
                    transferArrived(remaining, readyQRR, RRCnt);
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

            Process current = readyQRR.peek();
            int remainingBurst = current.getRemainigBurstTime();
            if (remainingBurst <= quantum) {
                current.setRemainigBurstTime(0);
                String PID = "P" + current.getPid();
                GanttBlock box = new GanttBlock(PID, RRCnt, RRCnt + remainingBurst);
                gantt.add(box);
                RRCnt += remainingBurst;
            } else {
                current.setRemainigBurstTime(remainingBurst - quantum);
                String PID = "P" + current.getPid();
                GanttBlock box = new GanttBlock(PID, RRCnt, RRCnt + quantum);
                gantt.add(box);
                RRCnt += quantum;
            }

            if (current.getRemainigBurstTime() == 0) {
                current.setFinishTime(RRCnt);
                current.setTurnaroundTime(current.getFinishTime() - current.getArrivalTime());
                current.setWaitingTime(current.getFinishTime() - current.getArrivalTime() - current.getBrustTime());
                readyQRR.poll();
                transferArrived(remaining, readyQRR, RRCnt);
            } else {
                readyQRR.add(readyQRR.poll());
                transferArrived(remaining, readyQRR, RRCnt);
            }
        }

        return calcAVGRR(gantt, allProcesses);
    }

    private void transferArrived(ArrayList<Process> source, Queue<Process> readyQ, int currentTime) {
        Iterator<Process> iterator = source.iterator();
        while (iterator.hasNext()) {
            Process p = iterator.next();
            if (p.getArrivalTime() <= currentTime) {
                readyQ.add(p);
                iterator.remove();
            }
        }
    }

    private ScheduleResult calcAVGRR(ArrayList<GanttBlock> gantt, ArrayList<Process> allProcesses) {
        double sumTAT = 0;
        double sumRT = 0;
        double sumWT = 0;
        int n = allProcesses.size();
        for (int i = 0; i < n; i++) {
            sumTAT += allProcesses.get(i).getTurnaroundTime();
            sumRT += (allProcesses.get(i).getResponseTime() - allProcesses.get(i).getArrivalTime());
            sumWT += allProcesses.get(i).getWaitingTime();
        }
        return new ScheduleResult(gantt, allProcesses, sumWT / n, sumTAT / n, sumRT / n);
    }

}