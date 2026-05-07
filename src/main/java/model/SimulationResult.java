package model;

public class SimulationResult {

    private final ScheduleResult rrResult;
    private final ScheduleResult pqResult;

    public SimulationResult(ScheduleResult rrResult, ScheduleResult pqResult) {
        this.rrResult = rrResult;
        this.pqResult = pqResult;
    }

    public ScheduleResult getRrResult() {
        return rrResult;
    }

    public ScheduleResult getPqResult() {
        return pqResult;
    }
}