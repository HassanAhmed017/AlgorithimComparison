package algorithm;

import java.util.*;

import model.GanttBlock;
import model.Process;
import model.ScheduleResult;

public class PriorityScheduler {

    private static void updateGantt(ArrayList<GanttBlock> gantt, String pid, int time) {
        if (!gantt.isEmpty() && gantt.get(gantt.size() - 1).getPid().equals(pid)) {
            int startTime = gantt.get(gantt.size() - 1).getStartTime();
            gantt.remove(gantt.size() - 1);
            gantt.add(new GanttBlock(pid, startTime, time + 1));
        } else {
            gantt.add(new GanttBlock(pid, time, time + 1));
        }
    }

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
                if (processesPQ.get(i).getArrivalTime() <= time && remaining[i] > 0) {
                    if (processesPQ.get(i).getPriority() < bestPriority) {
                        bestPriority = processesPQ.get(i).getPriority();
                    }
                }
            }

            ArrayList<Integer> readyList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (processesPQ.get(i).getArrivalTime() <= time && remaining[i] > 0
                        && processesPQ.get(i).getPriority() == bestPriority) {
                    readyList.add(i);
                }
            }

            if (readyList.isEmpty()) {
                updateGantt(gantt, "IDLE", time);
                time++;
                qCounter = 0;
                continue;
            }

            int selected = -1;
            if (lastIdx != -1 &&
                    readyList.contains(lastIdx) &&
                    qCounter < quantum &&
                    processesPQ.get(lastIdx).getPriority() == bestPriority) {
                selected = lastIdx;
            } else {
                int oldest = Integer.MAX_VALUE;
                for (int idx : readyList) {
                    if (lastExecution[idx] < oldest) {
                        oldest = lastExecution[idx];
                        selected = idx;
                    }
                }
                qCounter = 0;
            }

            Process p = processesPQ.get(selected);

            if (!started[selected]) {
                response[selected] = time;
                started[selected] = true;
            }

            updateGantt(gantt, "P" + p.getPid(), time);

            remaining[selected]--;
            p.setRemainigBurstTime(remaining[selected]);
            time++;
            qCounter++;
            lastExecution[selected] = time;
            lastIdx = selected;

            if (remaining[selected] == 0) {
                completed++;
                p.setFinishTime(time);
                turnaround[selected] = p.getFinishTime() - p.getArrivalTime();
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
            totalR += (response[i] - processesPQ.get(i).getArrivalTime());
        }

        return new ScheduleResult(
                gantt,
                processesPQ,
                totalW / n,
                totalT / n,
                totalR / n
        );
    }
}
