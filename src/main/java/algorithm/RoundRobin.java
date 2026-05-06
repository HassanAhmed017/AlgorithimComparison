package algorithm;

import java.util.*;

import model.Process;

public class RoundRobin {
    private Queue<Process> readyQRR = new LinkedList<>();

    private int RRCnt = 0;//start of the process

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            printRQRR();
            if (readyQRR.isEmpty()) {
                timer.cancel();
            }
        }
    };

    void startRR(int quantum, ArrayList<Process> processes) {
        initializeReadyQ(processes);
        timer.schedule(task, 0, 10000); //used to print the RQ every 10 seconds during runtime

        while (!readyQRR.isEmpty()) {
            if (RRCnt < readyQRR.peek().getArrivalTime()) {
                RRCnt = readyQRR.peek().getArrivalTime();
            }

            if (readyQRR.peek().getResponseTime() == -1) {
                readyQRR.peek().setResponseTime(RRCnt);
            }

            int remaining = readyQRR.peek().getRemainigBurstTime();
            if (remaining <= quantum) {
                readyQRR.peek().setRemainigBurstTime(0);
                RRCnt += remaining;
            } else {
                readyQRR.peek().setRemainigBurstTime(remaining - quantum);
                RRCnt += quantum;
            }

            //turnAround & waiting times
            if (readyQRR.peek().getRemainigBurstTime() == 0){
                //TurnAroundTime
                readyQRR.peek().setFinishTime(RRCnt);
                readyQRR.peek().setTurnaroundTime(readyQRR.peek().getFinishTime() - readyQRR.peek().getArrivalTime());
                //WaitingTime = finishTime - ArrivalTime - BurstTime
                readyQRR.peek().setWaitingTime(readyQRR.peek().getFinishTime() - readyQRR.peek().getArrivalTime() - readyQRR.peek().getBrustTime());
                readyQRR.poll();
            } else {
                readyQRR.add(readyQRR.peek());
                readyQRR.poll();
            }



        }

    }

    void initializeReadyQ(ArrayList<Process> processes) {
        for (int i = 0; i < processes.size(); i++) {
            readyQRR.add(processes.get(i));
        }
    }

    void printRQRR() {
        System.out.println(readyQRR);
    }


}
//write a function for PQ of same P