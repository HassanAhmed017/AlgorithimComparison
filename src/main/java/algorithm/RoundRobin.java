package algorithm;
import java.util.*;
import model.Process;

public class RoundRobin {
    private Queue<Process> readyQRR = new LinkedList<>();

    private int RRCnt = 0;//start of the process

    void startRR(int quantum, ArrayList<Process> processes){
        initializeReadyQ(processes);
        System.out.println(readyQRR); //here we print ready queue for the first time (remove sout in gui)

        while (!readyQRR.isEmpty()){
            if(RRCnt < readyQRR.peek().getArrivalTime()){
                RRCnt = readyQRR.peek().getArrivalTime();
            }


            readyQRR.peek().setRemainigBurstTime(readyQRR.peek().getRemainigBurstTime()-quantum);
            if(readyQRR.peek().getResponseTime() == -1){
                readyQRR.peek().setResponseTime(RRCnt);
            }

            //turnAround & waiting times
            if (readyQRR.peek().getRemainigBurstTime() == 0){
                //TurnAroundTime
                readyQRR.peek().setFinishTime(RRCnt+quantum);
                readyQRR.peek().setTurnaroundTime(readyQRR.peek().getArrivalTime() - readyQRR.peek().getFinishTime());
                //WaitingTime = finishTime - ArrivalTime - BurstTime
                readyQRR.peek().setWaitingTime(readyQRR.peek().getFinishTime()-readyQRR.peek().getArrivalTime()-readyQRR.peek().getBrustTime());
            }

            RRCnt+=quantum;

        }

    }

    void initializeReadyQ(ArrayList<Process> processes){
        for(int i = 0 ; i < processes.size() ; i++){
            readyQRR.add(processes.get(i));
        }
    }
}


//write a function for PQ of same P