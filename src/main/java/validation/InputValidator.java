package validation;

import java.util.*;
import model.Process;

public class InputValidator {

    public static boolean validateProcess(Process p, List<Process> processes) {

        if (p == null) {
            throw new IllegalArgumentException("Process cannot be null.");
        }

        if (p.getPid() < 0) {
            throw new IllegalArgumentException("Process ID cannot be negative.");
        }

        if (isDuplicateId(p.getPid(), processes)) {
            throw new IllegalArgumentException("Duplicate Process ID: " + p.getPid());
        }

        if (p.getArrivalTime() < 0) {
            throw new IllegalArgumentException("Arrival time cannot be negative.");
        }

        if (p.getBurstTime() <= 0) {
            throw new IllegalArgumentException("Burst time must be greater than 0.");
        }


        if (p.getPriority() < 1 || p.getPriority() > 10) {
            throw new IllegalArgumentException(
                    "Priority must be between 1 and 10 (1 = highest).");
        }
        return false;
    }

    public static boolean isDuplicateId(int pid, List<Process> processes) {
        if (processes == null) return false;

        for (Process p : processes) {
            if (p.getPid() == pid) {
                return true;
            }
        }
        return false;
    }

    public static void validateQuantum(int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum must be greater than 0.");
        }
    }

    public static boolean validateProcessList(List<Process> processes) {

        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException("Process list cannot be empty.");
        }

        Set<Integer> ids = new HashSet<>();

        for (Process p : processes) {

            if (p == null) {
                throw new IllegalArgumentException("Process list contains null.");
            }

            if (ids.contains(p.getPid())) {
                throw new IllegalArgumentException("Duplicate Process IDs found.");
            }
            ids.add(p.getPid());

            if (p.getArrivalTime() < 0) {
                throw new IllegalArgumentException(
                        "Process " + p.getPid() + " has invalid arrival time.");
            }

            if (p.getBurstTime() <= 0) {
                throw new IllegalArgumentException(
                        "Process " + p.getPid() + " has invalid burst time.");
            }


            if (p.getPriority() < 1 || p.getPriority() > 10) {
                throw new IllegalArgumentException(
                        "Process " + p.getPid() + " has invalid priority (must be 1-10).");
            }
        }
        return false;
    }
}