package algorithm;
import java.util.*;
import model.Process;
import model.ScheduleResult;
import model.SimulationResult;

import static validation.InputValidator.*;


public class Scheduler {

    static Scanner in = new Scanner(System.in);
    public static ArrayList<Process> processes = new ArrayList<Process>();
    public static int quantum = 2;

    static ArrayList<Process> deepCopy(ArrayList<Process> original) {
        ArrayList<Process> copy = new ArrayList<>();
        for (Process p : original) {
            Process newP = new Process(
                    p.getPid(),
                    p.getArrivalTime(),
                    p.getBrustTime(),
                    p.getPriority()
            );
            newP.setResponseTime(-1);
            copy.add(newP);
        }
        return copy;
    }


    static void createProcess() {
        System.out.println("Enter process ID: ");
        int processID = in.nextInt();

        System.out.println("Enter process Arival Time: ");
        int processArivalTime = in.nextInt();

        System.out.println("Enter process burst Time: ");
        int processburstTime = in.nextInt();

        System.out.println("Enter process Priority: ");
        int processPriority = in.nextInt();

        Process newP = new Process(processID, processArivalTime, processburstTime, processPriority);
        try {

            validateProcess(newP, processes);

            newP.setResponseTime(-1);
            processes.add(newP);

            System.out.println("Process added successfully.");

        } catch (IllegalArgumentException e) {

            System.out.println("Validation Error: " + e.getMessage());
        }
    }


    public static void removeProcess(int id) {
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getPid() == id) {
                processes.remove(i);  // remove directly by index
                break;
            }
        }
    }


    public static void Clear() {
        processes.clear();
    }

    static void enterQuantum() {
        int temp = in.nextInt();
        validateQuantum(temp);
        quantum = temp;
    }


    public static SimulationResult start() {
        Process[] p_array = processes.toArray(new Process[processes.size()]);
        Arrays.sort(p_array, Comparator.comparingInt(p -> p.getArrivalTime()));
        processes = new ArrayList<>(Arrays.asList(p_array));

        validateProcessList(processes);

        ArrayList<Process> processesRR = deepCopy(processes);
        ArrayList<Process> processesPQ = deepCopy(processes);

        RoundRobin rr = new RoundRobin();
        ScheduleResult rrResult = rr.startRR(quantum, processesRR);

        PriorityScheduler pq = new PriorityScheduler();
        ScheduleResult pqResult = pq.schedule(processesPQ, quantum);

        return new SimulationResult(rrResult, pqResult);
    }


    public static void main(String[] args) {
        int choice;
        while (true) { //added for testing gets removed when gui
            System.out.println("=== Scheduler Menu ==="); //shuld be removed when gui is added
            System.out.println("1. create process");
            System.out.println("2. delete process");
            System.out.println("3. clear processes");
            System.out.println("4. Enter Quantum");
            System.out.println("5. start simulation");
            System.out.println("6. Exit"); //for testing
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
                case 6: //for testing
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        }
    }

    public static SimulationResult loadScenarioA() {
        Clear();
        quantum = 3;

        processes.add(new Process(1, 0, 5, 3));
        processes.add(new Process(2, 1, 3, 1));
        processes.add(new Process(3, 2, 8, 2));
        processes.add(new Process(4, 3, 2, 4));
        processes.add(new Process(5, 4, 6, 2));

        for (Process p : processes) p.setResponseTime(-1);

        return start();
    }

    public static SimulationResult loadScenarioB() { //will use RR to solve this urgancy case && there is variaty in priorities
        Clear();
        quantum = 3;

        processes.add(new Process(1, 0, 10, 4));
        processes.add(new Process(2, 1, 8, 4));
        processes.add(new Process(3, 2, 4, 1));
        processes.add(new Process(4, 3, 6, 1));
        processes.add(new Process(5, 4, 3, 2));

        for (Process p : processes) p.setResponseTime(-1);

        return start();
    }

    public static SimulationResult loadScenarioC() {//is RR fair?
        Clear();
        quantum = 3;

        processes.add(new Process(1, 0, 9, 2));
        processes.add(new Process(2, 0, 9, 2));
        processes.add(new Process(3, 0, 9, 2));
        processes.add(new Process(4, 0, 9, 2));

        for (Process p : processes) p.setResponseTime(-1);

        return start();
    }

    public static SimulationResult loadScenarioD() {//low priority waits more
        Clear();
        quantum = 3;

        processes.add(new Process(1, 0, 3, 1));
        processes.add(new Process(2, 0, 3, 1));
        processes.add(new Process(3, 0, 3, 1));
        processes.add(new Process(4, 0, 3, 1));
        processes.add(new Process(5, 0, 10, 4));

        for (Process p : processes) p.setResponseTime(-1);

        return start();
    }
}