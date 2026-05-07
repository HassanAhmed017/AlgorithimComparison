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

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            printRQRR();
        }
    };

    ScheduleResult startRR(int quantum, ArrayList<Process> processes) {
        initializeReadyQ(processes);

        timer.schedule(task, 0, 5000); //used to print the RQ every 10 seconds during runtime

        while (!readyQRR.isEmpty()) {
            if (RRCnt < readyQRR.peek().getArrivalTime()) {
                GanttBlock idle = new GanttBlock("IDLE", RRCnt , readyQRR.peek().getArrivalTime());
                gantt.add(idle);
                RRCnt = readyQRR.peek().getArrivalTime();
            }

            if (readyQRR.peek().getResponseTime() == -1) {
                readyQRR.peek().setResponseTime(RRCnt);
            }

            int remaining = readyQRR.peek().getRemainigBurstTime();
            if (remaining <= quantum) {
                readyQRR.peek().setRemainigBurstTime(0);
                String PID = String.valueOf(readyQRR.peek().getPid());
                int endTimeBox = RRCnt + remaining ;
                GanttBlock box = new GanttBlock(PID , RRCnt , endTimeBox);
                gantt.add(box);
                RRCnt += remaining;
            } else {
                readyQRR.peek().setRemainigBurstTime(remaining - quantum);
                String PID = String.valueOf(readyQRR.peek().getPid());
                int endTimeBox = RRCnt + quantum ;
                GanttBlock box = new GanttBlock(PID , RRCnt , endTimeBox);
                gantt.add(box);
                RRCnt += quantum;
            }


            if (readyQRR.peek().getRemainigBurstTime() == 0){
                //TurnAroundTime
                readyQRR.peek().setFinishTime(RRCnt);
                readyQRR.peek().setTurnaroundTime(readyQRR.peek().getFinishTime() - readyQRR.peek().getArrivalTime());
                //WaitingTime = finishTime - ArrivalTime - BurstTime
                readyQRR.peek().setWaitingTime(readyQRR.peek().getFinishTime() - readyQRR.peek().getArrivalTime() - readyQRR.peek().getBrustTime());
                readyQRR.poll();
            }
            else{
                readyQRR.add(readyQRR.peek());
                readyQRR.poll();
            }


        }
        ScheduleResult resultRR = calcAVGRR(processes);

        timer.cancel();
        return resultRR;
    }

    void initializeReadyQ(ArrayList<Process> processes) {
        for (int i = 0; i < processes.size(); i++) {
            readyQRR.add(processes.get(i));
        }
    }

    void printRQRR() {
        System.out.println("Current Ready Queue: "+ readyQRR);
    }

    ScheduleResult calcAVGRR(ArrayList<Process> processes){
        double sumTAT = 0;
        double sumRT = 0;
        double sumWT = 0;
        int n = processes.size();
        for (int i = 0 ; i < n ; i++){
            sumTAT += processes.get(i).getTurnaroundTime();
            sumRT += processes.get(i).getResponseTime();
            sumWT += processes.get(i).getWaitingTime();
        }
        return new ScheduleResult(
                gantt, processes, sumWT / n, sumTAT / n, sumRT / n);
    }

}