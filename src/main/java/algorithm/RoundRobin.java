package algorithm;
import java.util.*;
import model.Process;

public class RoundRobin {
    private Queue<Process> readyQRR = new LinkedList<>();

    void startRR(int Q, ArrayList<Process> processes){
        initializeReadyQ(processes);
        System.out.println(readyQRR); //here we print ready queue for the first time (remove sout in gui)


    }

    void initializeReadyQ(ArrayList<Process> processes){
        for(int i = 0 ; i < processes.size() ; i++){
            readyQRR.add(processes.get(i));
        }
    }
}