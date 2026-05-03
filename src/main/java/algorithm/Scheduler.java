package algorithm;
import java.util.*;

import model.Process;


public class Scheduler {
    static Scanner in = new Scanner(System.in);
    static ArrayList<Process> processes = new ArrayList<Process>();
    static int quantum = 2;

    static void createProcess(){
        System.out.println("Enter process ID: ");
        int processID = in.nextInt();

        System.out.println("Enter process Arival Time: ");
        int processArivalTime = in.nextInt();

        System.out.println("Enter process burst Time: ");
        int processburstTime = in.nextInt();

        System.out.println("Enter process Priority: ");
        int processPriority = in.nextInt();

        Process newP = new Process(processID ,processArivalTime, processburstTime, processPriority);

        processes.add(newP);
    }

    static void removeProcess(int id){
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getPid() == id) {
                processes.remove(i);  // remove directly by index
                break;
            }
        }
    }

    static void Clear(){
        processes.clear();
    }
    static void enterQuantum(){
        int quantum = in.nextInt();
    }
    static  void start(){
        Process[] p_array = processes.toArray(new Process[processes.size()]);
        Arrays.sort(p_array , Comparator.comparingInt(p -> p.getArrivalTime()));
        processes = new ArrayList<>(Arrays.asList(p_array)) ;
        ArrayList<Process> processesrRR = new ArrayList<>(processes);
        ArrayList<Process> processesrPQ = new ArrayList<>(processes);

    }


    public static void main(String[] args) {

        int choice;
        //menu
        System.out.println("=== Scheduler Menu ==="); //shuld be removed when gui is added
        System.out.println("1. create process");
        System.out.println("2. delete process");
        System.out.println("3. clear processes");
        System.out.println("4. Enter Quantum");
        System.out.println("5. start simulation");
        System.out.print("Enter your choice: ");
        choice = in.nextInt();

        switch (choice) {
            case 1:
                 createProcess();
                break;
            case 2:
                System.out.println("Enter the ID of the process you want to remove:");
                int id = in.nextInt();
                removeProcess(id);
                break;
            case 3:
                Clear();
                break;
            case 4:
                enterQuantum();
                break;
            case 5:
                start();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }

    }




}
