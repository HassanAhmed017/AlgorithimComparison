package algorithm;

import java.util.*;

import model.GanttBlock;
import model.Process;
import model.ScheduleResult;
import model.Resource;

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

        // Priority Inheritance tracking
        int[] originalPriority = new int[n];

        boolean[] holdsResource = new boolean[n];
        boolean[] needsResource = new boolean[n];
        boolean[] blocked = new boolean[n];

        for (int i = 0; i < n; i++) {
            remaining[i] = processesPQ.get(i).getRemainigBurstTime();
            lastExecution[i] = -1;
            originalPriority[i] = processesPQ.get(i).getPriority();
        }

        // Setup Priority Inversion scenario
        Resource sharedResource = new Resource("LockA");

        boolean resourceScenario = false;

        int lowestPriIdx = -1;
        int highestPriIdx = -1;

        int lowestPriVal = Integer.MIN_VALUE;
        int highestPriVal = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {

            int pri = processesPQ.get(i).getPriority();

            if (pri > lowestPriVal) {
                lowestPriVal = pri;
                lowestPriIdx = i;
            }

            if (pri < highestPriVal) {
                highestPriVal = pri;
                highestPriIdx = i;
            }
        }

        if (lowestPriIdx != highestPriIdx && n >= 2) {

            resourceScenario = true;

            holdsResource[lowestPriIdx] = true;
            needsResource[highestPriIdx] = true;

            sharedResource.setHolder(processesPQ.get(lowestPriIdx));

            System.out.println(
                    "[Resource Setup] P"
                            + processesPQ.get(lowestPriIdx).getPid()
                            + " holds LockA | P"
                            + processesPQ.get(highestPriIdx).getPid()
                            + " needs LockA");
        }

        int time = 0;
        int completed = 0;
        int lastIdx = -1;
        int qCounter = 0;

        while (completed < n) {

            // Apply Priority Inheritance
            if (resourceScenario) {

                for (int i = 0; i < n; i++) {

                    if (!needsResource[i]
                            || processesPQ.get(i).getArrivalTime() > time) {
                        continue;
                    }
                    Process waiter = processesPQ.get(i);

                    if (sharedResource.isLocked()) {

                        blocked[i] = true;

                        int holderIdx = -1;

                        for (int j = 0; j < n; j++) {
                            if (holdsResource[j]) {
                                holderIdx = j;
                                break;
                            }
                        }

                        if (holderIdx == -1) continue;

                        Process holder = processesPQ.get(holderIdx);

                        if (waiter.getPriority() < holder.getPriority()) {

                            System.out.println(
                                    "[Priority Inheritance] P"
                                            + holder.getPid()
                                            + " boosted from "
                                            + holder.getPriority()
                                            + " to "
                                            + waiter.getPriority());

                            holder.setPriority(waiter.getPriority());
                        }
                    }
                }
            }

            int bestPriority = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (blocked[i]) continue;
                if (processesPQ.get(i).getArrivalTime() <= time && remaining[i] > 0) {
                    if (processesPQ.get(i).getPriority() < bestPriority) {
                        bestPriority = processesPQ.get(i).getPriority();
                    }
                }
            }

            ArrayList<Integer> readyList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (blocked[i]) continue;
                if (processesPQ.get(i).getArrivalTime() <= time && remaining[i] > 0 && processesPQ.get(i).getPriority() == bestPriority) {
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
            if (lastIdx != -1 && readyList.contains(lastIdx) && qCounter < quantum) {
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

                // Release resource
                if (resourceScenario && holdsResource[selected]) {

                    System.out.println(
                            "[Resource Released] P"
                                    + p.getPid()
                                    + " released LockA");

                    p.setPriority(originalPriority[selected]);

                    holdsResource[selected] = false;

                    sharedResource.release(originalPriority[selected]);

                    System.out.println(
                            "[Priority Restored] P"
                                    + p.getPid()
                                    + " restored to "
                                    + originalPriority[selected]);

                    for (int i = 0; i < n; i++) {

                        if (needsResource[i] && blocked[i]) {

                            blocked[i] = false;
                            needsResource[i] = false;

                            System.out.println(
                                    "[Unblocked] P"
                                            + processesPQ.get(i).getPid());
                        }
                    }
                }
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