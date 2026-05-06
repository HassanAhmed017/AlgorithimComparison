package algorithm;
import java.util.*;
import model.GanttBlock;
import model.Process;
import model.ScheduleResult;

public class PriorityScheduler {
    public static ScheduleResult schedule(ArrayList<Process> processesPQ, int quantum) {

        ArrayList<GanttBlock> gantt = new ArrayList<>();
        int n = processesPQ.size();

        int[] remaining = new int[n];
        int[] response = new int[n];
        int[] waiting = new int[n];
        int[] turnaround = new int[n];

        boolean[] started = new boolean[n];
        int[] lastExecution = new int[n];

        for (int i = 0; i < n; i++) {
            remaining[i] = processesPQ.get(i).getRemainigBurstTime();
            lastExecution[i] = -1;
        }

        int time = 0;
        int completed = 0;
        int lastIdx = -1;
        int qCounter = 0;

        while (completed < n) {

            int bestPriority = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Process p = processesPQ.get(i);
                if (p.getArrivalTime() <= time && remaining[i] > 0) {
                    if (p.getPriority() < bestPriority) {
                        bestPriority = p.getPriority();
                    }
                }
            }

            ArrayList<Integer> readyList = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                Process p = processesPQ.get(i);
                if (p.getArrivalTime() <= time && remaining[i] > 0 && p.getPriority() == bestPriority) {
                    readyList.add(i);
                }
            }

            if (readyList.isEmpty()) {
                time++;
                continue;
            }

            int selected = -1;

            if (lastIdx != -1 && readyList.contains(lastIdx) && qCounter < quantum) {
                selected = lastIdx;
            } else {
                int oldest = Integer.MAX_VALUE;
                for (int j = 0; j < readyList.size(); j++) {
                    int idx = readyList.get(j);

                    if (lastExecution[idx] < oldest) {
                        oldest = lastExecution[idx];
                        selected = idx;
                    }
                }
                qCounter = 0;
            }

            Process p = processesPQ.get(selected);

            if (!started[selected]) {
                response[selected] = time - p.getArrivalTime();
                started[selected] = true;
            }

            String pid = "P" + p.getPid();
            if (!gantt.isEmpty() && gantt.get(gantt.size() - 1).getPid().equals(pid)) {
                GanttBlock last = gantt.get(gantt.size() - 1);
                gantt.set(gantt.size() - 1, new GanttBlock(pid, last.getStartTime(), time + 1));
            } else {
                gantt.add(new GanttBlock(pid, time, time + 1));
            }

            remaining[selected]--;
            time++;
            qCounter++;
            lastExecution[selected] = time;
            lastIdx = selected;

            if (remaining[selected] == 0) {
                completed++;

                turnaround[selected] = time - p.getArrivalTime();
                waiting[selected] = turnaround[selected] - p.getBrustTime();

                p.setResponseTime(response[selected]);
                p.setWaitingTime(waiting[selected]);
                p.setTurnaroundTime(turnaround[selected]);

                lastIdx = -1;
                qCounter = 0;
            }
        }

        double totalW = 0, totalT = 0, totalR = 0;
        for (int i = 0; i < n; i++) {
            totalW += waiting[i];
            totalT += turnaround[i];
            totalR += response[i];
        }

        return new ScheduleResult(
                gantt, null, null, null, null,
                totalW / n,
                totalT / n,
                totalR / n
        );
    }

}
