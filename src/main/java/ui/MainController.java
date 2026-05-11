package ui;

import algorithm.Scheduler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.util.Duration;
import model.GanttBlock;
import model.Process;
import model.ScheduleResult;
import model.SimulationResult;
import validation.InputValidator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // ── Input Fields ──────────────────────────────────────────
    @FXML private TextField tfPid;
    @FXML private TextField tfArrival;
    @FXML private TextField tfBurst;
    @FXML private TextField tfPriority;
    @FXML private TextField tfQuantum;

    // ── Process Table ─────────────────────────────────────────
    @FXML private TableView<Process>            processTable;
    @FXML private TableColumn<Process, Integer> colPid;
    @FXML private TableColumn<Process, Integer> colArrival;
    @FXML private TableColumn<Process, Integer> colBurst;
    @FXML private TableColumn<Process, Integer> colPriority;

    // ── Ready Queue Labels ────────────────────────────────────
    @FXML private Label rrQueueLabel;
    @FXML private Label pqQueueLabel;

    // ── Gantt Panes ───────────────────────────────────────────
    @FXML private Pane       rrGanttPane;
    @FXML private ScrollPane rrGanttScroll;
    @FXML private Pane       pqGanttPane;
    @FXML private ScrollPane pqGanttScroll;

    // ── RR Results Table ──────────────────────────────────────
    @FXML private TableView<String[]>           rrResultTable;
    @FXML private TableColumn<String[], String> rrColPid;
    @FXML private TableColumn<String[], String> rrColFt;
    @FXML private TableColumn<String[], String> rrColTat;
    @FXML private TableColumn<String[], String> rrColWt;
    @FXML private TableColumn<String[], String> rrColRt;
    @FXML private Label rrAvgLabel;

    // ── Priority Results Table ────────────────────────────────
    @FXML private TableView<String[]>           pqResultTable;
    @FXML private TableColumn<String[], String> pqColPid;
    @FXML private TableColumn<String[], String> pqColFt;
    @FXML private TableColumn<String[], String> pqColTat;
    @FXML private TableColumn<String[], String> pqColWt;
    @FXML private TableColumn<String[], String> pqColRt;
    @FXML private Label pqAvgLabel;

    // ── Comparison Table Labels ───────────────────────────────
    @FXML private Label rrAvgWtLabel;
    @FXML private Label rrAvgTatLabel;
    @FXML private Label rrAvgRtLabel;
    @FXML private Label pqAvgWtLabel;
    @FXML private Label pqAvgTatLabel;
    @FXML private Label pqAvgRtLabel;

    // ── Left Panel ────────────────────────────────────────────
    @FXML private Label bestAlgoLabel;
    @FXML private Label scenarioLabel;

    // ── Conclusion ────────────────────────────────────────────
    @FXML private VBox     conclusionContent;
    @FXML private TextArea conclusionText;

    // ── Internal State ────────────────────────────────────────
    private final ObservableList<Process> processList =
            FXCollections.observableArrayList();

    private Timeline rrQueueTimer;
    private Timeline pqQueueTimer;

    private static final String[] COLORS = {
            "#5B8DB8", "#6BAA75", "#C0695A", "#C09A5A",
            "#8B7BAA", "#5AAAB5", "#AA7B8B", "#5AB5AA",
            "#AA9B5A", "#8BAA5A"
    };

    // ── initialize() ─────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colPid.setCellValueFactory(new PropertyValueFactory<>("pid"));
        colArrival.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        colBurst.setCellValueFactory(new PropertyValueFactory<>("brustTime"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        processTable.setItems(processList);

        rrColPid.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        rrColFt.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[1]));
        rrColTat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        rrColWt.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[3]));
        rrColRt.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[4]));

        pqColPid.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        pqColFt.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[1]));
        pqColTat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        pqColWt.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[3]));
        pqColRt.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[4]));
    }

    // ── handleAddProcess() ────────────────────────────────────
    @FXML
    private void handleAddProcess() {
        try {
            int pid      = Integer.parseInt(tfPid.getText().trim());
            int arrival  = Integer.parseInt(tfArrival.getText().trim());
            int burst    = Integer.parseInt(tfBurst.getText().trim());
            int priority = Integer.parseInt(tfPriority.getText().trim());

            Process p = new Process(pid, arrival, burst, priority);
            InputValidator.validateProcess(p, processList);
            p.setResponseTime(-1);

            processList.add(p);
            tfPid.clear();
            tfArrival.clear();
            tfBurst.clear();
            tfPriority.clear();
            tfPid.requestFocus();

        } catch (NumberFormatException e) {
            showError("All fields must be valid integers.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // ── handleRemoveProcess() ─────────────────────────────────
    @FXML
    private void handleRemoveProcess() {
        Process selected = processTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a process row to remove.");
            return;
        }
        processList.remove(selected);
        Scheduler.removeProcess(selected.getPid());
    }

    // ── handleClear() ─────────────────────────────────────────
    @FXML
    private void handleClear() {
        stopTimers();
        processList.clear();
        Scheduler.Clear();

        rrGanttPane.getChildren().clear();
        pqGanttPane.getChildren().clear();
        rrResultTable.getItems().clear();
        pqResultTable.getItems().clear();

        rrQueueLabel.setText("Queue: [ ]");
        pqQueueLabel.setText("Queue: [ ]");
        rrAvgLabel.setText("Avg WT: —  |  Avg TAT: —  |  Avg RT: —");
        pqAvgLabel.setText("Avg WT: —  |  Avg TAT: —  |  Avg RT: —");

        String baseStyle =
                "-fx-text-fill: white;"
                        + "-fx-background-color: #1C3333;"
                        + "-fx-border-color: #4A7C7C;"
                        + "-fx-border-width: 1;"
                        + "-fx-padding: 6 10 6 10;";

        for (Label l : List.of(
                rrAvgWtLabel, rrAvgTatLabel, rrAvgRtLabel,
                pqAvgWtLabel, pqAvgTatLabel, pqAvgRtLabel)) {
            l.setText("—");
            l.setStyle(baseStyle);
        }

        bestAlgoLabel.setText("—");
        bestAlgoLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: white;");
        scenarioLabel.setText("—");

        if (conclusionContent != null)
            conclusionContent.getChildren().clear();
        conclusionText.setText("Run the simulation to generate the conclusion.");
        tfQuantum.clear();
    }

    // ── handleRun() ───────────────────────────────────────────
    @FXML
    private void handleRun() {
        try {
            int quantum = Integer.parseInt(tfQuantum.getText().trim());
            InputValidator.validateQuantum(quantum);
            Scheduler.quantum = quantum;
            Scheduler.processes = new ArrayList<>(processList);

            stopTimers();

            SimulationResult result = Scheduler.start();
            if (result == null) {
                showError("Simulation failed. Check your process list.");
                return;
            }
            displayResults(result);

        } catch (NumberFormatException e) {
            showError("Quantum must be a valid positive integer.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // ── Scenario Handlers ─────────────────────────────────────
    @FXML
    private void handleScenarioA() {
        stopTimers();
        SimulationResult result = Scheduler.loadScenarioA();
        if (result == null) { showError("Scenario A failed to load."); return; }
        syncProcessListFromScheduler();
        tfQuantum.setText(String.valueOf(Scheduler.quantum));
        displayResults(result);
    }

    @FXML
    private void handleScenarioB() {
        stopTimers();
        SimulationResult result = Scheduler.loadScenarioB();
        if (result == null) { showError("Scenario B failed to load."); return; }
        syncProcessListFromScheduler();
        tfQuantum.setText(String.valueOf(Scheduler.quantum));
        displayResults(result);
    }

    @FXML
    private void handleScenarioC() {
        stopTimers();
        SimulationResult result = Scheduler.loadScenarioC();
        if (result == null) { showError("Scenario C failed to load."); return; }
        syncProcessListFromScheduler();
        tfQuantum.setText(String.valueOf(Scheduler.quantum));
        displayResults(result);
    }

    @FXML
    private void handleScenarioD() {
        stopTimers();
        SimulationResult result = Scheduler.loadScenarioD();
        if (result == null) { showError("Scenario D failed to load."); return; }
        syncProcessListFromScheduler();
        tfQuantum.setText(String.valueOf(Scheduler.quantum));
        displayResults(result);
    }

    // ── syncProcessListFromScheduler() ────────────────────────
    private void syncProcessListFromScheduler() {
        processList.clear();
        processList.addAll(Scheduler.processes);
    }

    // ── displayResults() ──────────────────────────────────────
    private void displayResults(SimulationResult result) {
        ScheduleResult rr = result.getRrResult();
        ScheduleResult pq = result.getPqResult();

        displayRRResults(rr);
        displayPQResults(pq);
        updateLeftPanel(rr, pq);
        updateComparisonLabels(rr, pq);
        displayConclusion(rr, pq);
        startQueueTimers(rr, pq);
    }

    // ── displayRRResults() ────────────────────────────────────
    private void displayRRResults(ScheduleResult result) {
        drawGanttChart(rrGanttPane, result.getGanttBlocks());
        fillResultsTable(rrResultTable, result);
        rrAvgLabel.setText(String.format(Locale.US,
                "Avg WT: %.2f  |  Avg TAT: %.2f  |  Avg RT: %.2f",
                result.getAvgWaitingTime(),
                result.getAvgTurnaroundTime(),
                computeAvgRelativeRT(result)));
    }

    // ── displayPQResults() ────────────────────────────────────
    private void displayPQResults(ScheduleResult result) {
        drawGanttChart(pqGanttPane, result.getGanttBlocks());
        fillResultsTable(pqResultTable, result);
        pqAvgLabel.setText(String.format(Locale.US,
                "Avg WT: %.2f  |  Avg TAT: %.2f  |  Avg RT: %.2f",
                result.getAvgWaitingTime(),
                result.getAvgTurnaroundTime(),
                computeAvgRelativeRT(result)));
    }

    // ── computeAvgRelativeRT() ────────────────────────────────
    private double computeAvgRelativeRT(ScheduleResult result) {
        List<Process> ps = result.getProcesses();
        if (ps == null || ps.isEmpty()) return 0;
        double sum = 0;
        for (Process p : ps)
            sum += (p.getResponseTime() - p.getArrivalTime());
        return sum / ps.size();
    }

    // ── drawGanttChart() ──────────────────────────────────────
    private void drawGanttChart(Pane pane, List<GanttBlock> blocks) {
        pane.getChildren().clear();
        if (blocks == null || blocks.isEmpty()) return;

        double scale      = 50.0;
        double blockH     = 50.0;
        double labelY     = blockH + 16;
        double totalWidth = blocks.get(blocks.size() - 1).getEndTime() * scale + 80;

        pane.setPrefSize(totalWidth, blockH + 28);

        for (GanttBlock block : blocks) {
            double x   = block.getStartTime() * scale;
            double w   = block.getDuration()  * scale;
            String pid = block.getPid();

            String colorHex = pid.equals("IDLE")
                    ? "#4A5568"
                    : COLORS[Math.abs(pid.hashCode()) % COLORS.length];

            Rectangle rect = new Rectangle(x, 0, Math.max(w - 2, 1), blockH);
            rect.setFill(Color.web(colorHex, 0.9));
            rect.setStroke(Color.web(colorHex));
            rect.setStrokeWidth(1.5);
            rect.setArcWidth(6);
            rect.setArcHeight(6);

            Text pidText = new Text(pid);
            pidText.setFont(Font.font("System", FontWeight.BOLD, 12));
            pidText.setFill(Color.WHITE);
            pidText.setX(x + w / 2.0 - pid.length() * 4.0);
            pidText.setY(blockH / 2.0 + 5);

            Text startText = new Text(String.valueOf(block.getStartTime()));
            startText.setFont(Font.font("Monospace", 10));
            startText.setFill(Color.web("#A8C4C4"));
            startText.setX(x);
            startText.setY(labelY);

            pane.getChildren().addAll(rect, pidText, startText);
        }

        GanttBlock last = blocks.get(blocks.size() - 1);
        Text endText = new Text(String.valueOf(last.getEndTime()));
        endText.setFont(Font.font("Monospace", 10));
        endText.setFill(Color.web("#A8C4C4"));
        endText.setX(last.getEndTime() * scale);
        endText.setY(labelY);
        pane.getChildren().add(endText);
    }

    // ── fillResultsTable() ────────────────────────────────────
    private void fillResultsTable(TableView<String[]> table, ScheduleResult result) {
        ObservableList<String[]> rows = FXCollections.observableArrayList();

        for (Process p : result.getProcesses()) {
            int relRT = p.getResponseTime() - p.getArrivalTime();
            rows.add(new String[]{
                    String.format(Locale.US, "P%d", p.getPid()),
                    String.valueOf(p.getFinishTime()),
                    String.valueOf(p.getTurnaroundTime()),
                    String.valueOf(p.getWaitingTime()),
                    String.valueOf(relRT)
            });
        }

        rows.add(new String[]{
                "AVG", "—",
                fmt(result.getAvgTurnaroundTime()),
                fmt(result.getAvgWaitingTime()),
                fmt(computeAvgRelativeRT(result))
        });

        table.setItems(rows);
    }

    // ── updateLeftPanel() ─────────────────────────────────────
    private void updateLeftPanel(ScheduleResult rr, ScheduleResult pq) {

        double rrWT  = rr.getAvgWaitingTime();
        double pqWT  = pq.getAvgWaitingTime();
        double rrTAT = rr.getAvgTurnaroundTime();
        double pqTAT = pq.getAvgTurnaroundTime();
        double rrRT  = computeAvgRelativeRT(rr);
        double pqRT  = computeAvgRelativeRT(pq);

        int rrWins = 0, pqWins = 0;

        if (rrWT  < pqWT)  rrWins++; else if (pqWT  < rrWT)  pqWins++;
        if (rrTAT < pqTAT) rrWins++; else if (pqTAT < rrTAT) pqWins++;
        if (rrRT  < pqRT)  rrWins++; else if (pqRT  < rrRT)  pqWins++;

        if (rrWins > pqWins) {
            bestAlgoLabel.setText("Round Robin ");
            bestAlgoLabel.setStyle(
                    "-fx-font-size: 13px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: #4CAF50;");
        } else if (pqWins > rrWins) {
            bestAlgoLabel.setText("Priority Scheduling ");
            bestAlgoLabel.setStyle(
                    "-fx-font-size: 13px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: #FF6B6B;");
        } else {
            bestAlgoLabel.setText("Tie ");
            bestAlgoLabel.setStyle(
                    "-fx-font-size: 13px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: #FFD700;");
        }
    }

    // ── updateComparisonLabels() ──────────────────────────────
    private void updateComparisonLabels(ScheduleResult rr, ScheduleResult pq) {
        double rrRT = computeAvgRelativeRT(rr);
        double pqRT = computeAvgRelativeRT(pq);

        rrAvgWtLabel.setText(fmt(rr.getAvgWaitingTime()));
        rrAvgTatLabel.setText(fmt(rr.getAvgTurnaroundTime()));
        rrAvgRtLabel.setText(fmt(rrRT));
        pqAvgWtLabel.setText(fmt(pq.getAvgWaitingTime()));
        pqAvgTatLabel.setText(fmt(pq.getAvgTurnaroundTime()));
        pqAvgRtLabel.setText(fmt(pqRT));

        highlightBetter(rrAvgWtLabel,  pqAvgWtLabel,  rr.getAvgWaitingTime(),    pq.getAvgWaitingTime());
        highlightBetter(rrAvgTatLabel, pqAvgTatLabel, rr.getAvgTurnaroundTime(), pq.getAvgTurnaroundTime());
        highlightBetter(rrAvgRtLabel,  pqAvgRtLabel,  rrRT, pqRT);
    }

    private void highlightBetter(Label rrLabel, Label pqLabel,
                                 double rrVal,  double pqVal) {
        String base =
                "-fx-background-color: #1C3333;"
                        + "-fx-border-color: #4A7C7C;"
                        + "-fx-border-width: 1;"
                        + "-fx-padding: 6 10 6 10;";

        String win =
                "-fx-background-color: #1A3A1A;"
                        + "-fx-border-color: #4CAF50;"
                        + "-fx-border-width: 2;"
                        + "-fx-padding: 6 10 6 10;";

        if (rrVal < pqVal) {
            rrLabel.setStyle(win  + "-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            pqLabel.setStyle(base + "-fx-text-fill: white;");
        } else if (pqVal < rrVal) {
            pqLabel.setStyle(win  + "-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            rrLabel.setStyle(base + "-fx-text-fill: white;");
        } else {
            rrLabel.setStyle(base + "-fx-text-fill: #FFD700;");
            pqLabel.setStyle(base + "-fx-text-fill: #FFD700;");
        }
    }

    // ── displayConclusion() ───────────────────────────────────
    private void displayConclusion(ScheduleResult rr, ScheduleResult pq) {

        if (conclusionContent != null)
            conclusionContent.getChildren().clear();

        double rrWT  = rr.getAvgWaitingTime();
        double pqWT  = pq.getAvgWaitingTime();
        double rrTAT = rr.getAvgTurnaroundTime();
        double pqTAT = pq.getAvgTurnaroundTime();
        double rrRT  = computeAvgRelativeRT(rr);
        double pqRT  = computeAvgRelativeRT(pq);

        double wtDiff  = Math.abs(rrWT  - pqWT);
        double tatDiff = Math.abs(rrTAT - pqTAT);
        double rtDiff  = Math.abs(rrRT  - pqRT);

        String wtWinner  = rrWT  <= pqWT  ? "Round Robin" : "Priority Scheduling";
        String tatWinner = rrTAT <= pqTAT ? "Round Robin" : "Priority Scheduling";
        String rtWinner  = rrRT  <= pqRT  ? "Round Robin" : "Priority Scheduling";
        String wtLoser   = rrWT  <= pqWT  ? "Priority Scheduling" : "Round Robin";
        String tatLoser  = rrTAT <= pqTAT ? "Priority Scheduling" : "Round Robin";

        // ── Q&A rows ─────────────────────────────────────────

        addAnalysisRow(
                "Which algorithm had the shorter average waiting time?",
                wtWinner + " kept processes waiting less on average — "
                        + fmt(Math.min(rrWT, pqWT)) + " vs "
                        + fmt(Math.max(rrWT, pqWT)) + " units"
                        + (wtDiff < 1.0 ? ", though the gap was very small." : ".")
        );

        addAnalysisRow(
                "Which algorithm completed processes faster overall?",
                tatWinner + " achieved a lower average turnaround time ("
                        + fmt(Math.min(rrTAT, pqTAT)) + " vs "
                        + fmt(Math.max(rrTAT, pqTAT)) + " units), meaning "
                        + "processes spent less total time in the system."
        );

        addAnalysisRow(
                "Which algorithm responded to processes more quickly?",
                rtWinner + " gave processes their first CPU slot sooner on average ("
                        + fmt(Math.min(rrRT, pqRT)) + " vs "
                        + fmt(Math.max(rrRT, pqRT)) + " units"
                        + (rtDiff < 1.0 ? ", a negligible difference)." : ").")
        );

        addAnalysisRow(
                "Did high-priority processes get a real advantage?",
                "Yes — Priority Scheduling intentionally serves urgent processes first, "
                        + "so they finish much sooner than they would under Round Robin's "
                        + "fixed time-slice approach."
        );

        addAnalysisRow(
                "Was Round Robin fair to all processes?",
                "Yes — every process received CPU time in rotation, regardless of priority. "
                        + "No process was skipped or delayed because of a lower priority number."
        );

        addAnalysisRow(
                "Could any process starve under Priority Scheduling?",
                "Potentially yes — if high-priority processes keep arriving, a low-priority "
                        + "process may wait indefinitely. Round Robin avoids this by guaranteeing "
                        + "every process gets a turn."
        );

        // ── Written conclusion ────────────────────────────────

        int rrWins = 0, pqWins = 0;
        if (rrWT  < pqWT)  rrWins++; else if (pqWT  < rrWT)  pqWins++;
        if (rrTAT < pqTAT) rrWins++; else if (pqTAT < rrTAT) pqWins++;
        if (rrRT  < pqRT)  rrWins++; else if (pqRT  < rrRT)  pqWins++;

        String overallWinner =
                rrWins > pqWins ? "Round Robin" :
                        pqWins > rrWins ? "Priority Scheduling" : null;

        StringBuilder sb = new StringBuilder();
        sb.append("CONCLUSION\n\n");

        if (overallWinner != null) {
            sb.append(overallWinner)
                    .append(" came out ahead in this simulation, winning ")
                    .append(Math.max(rrWins, pqWins))
                    .append(" out of 3 measured metrics (waiting time, turnaround time, and response time).\n\n");
        } else {
            sb.append("Both algorithms performed equally well across the three measured metrics — "
                    + "this workload does not clearly favor either approach.\n\n");
        }

        if (wtDiff < 0.5) {
            sb.append("Waiting time was nearly identical between the two algorithms (")
                    .append(fmt(rrWT)).append(" for RR vs ")
                    .append(fmt(pqWT)).append(" for Priority), suggesting this workload does not "
                            + "strongly benefit from prioritization.\n\n");
        } else {
            sb.append(wtWinner).append(" reduced average waiting time by ")
                    .append(fmt(wtDiff)).append(" units compared to ")
                    .append(wtLoser).append(".\n\n");
        }

        if (tatDiff < 0.5) {
            sb.append("Turnaround time was also very close, meaning both algorithms completed "
                    + "this set of processes in roughly the same total time.\n\n");
        } else {
            sb.append(tatWinner).append(" finished processes ")
                    .append(fmt(tatDiff))
                    .append(" units sooner on average — a meaningful improvement in throughput "
                            + "for this workload.\n\n");
        }

        sb.append("From a fairness perspective, Round Robin guaranteed every process received "
                + "CPU time regularly, making it the safer choice when no process should be "
                + "left waiting indefinitely. Priority Scheduling, while faster for urgent tasks, "
                + "introduces the risk of starvation for lower-priority processes if the workload "
                + "is not carefully balanced.\n\n");

        sb.append("In summary: use Round Robin for interactive or time-sharing systems where "
                + "fairness matters most. Use Priority Scheduling for batch or real-time systems "
                + "where certain tasks genuinely need to finish first.");

        conclusionText.setText(sb.toString());
    }

    // ── addAnalysisRow() ──────────────────────────────────────
    private void addAnalysisRow(String question, String answer) {
        VBox row = new VBox(4);
        row.setStyle(
                "-fx-background-color: #1C3333;"
                        + "-fx-border-color: #4A7C7C;"
                        + "-fx-border-radius: 4;"
                        + "-fx-background-radius: 4;");
        row.setPadding(new Insets(8));

        Label q = new Label("Q: " + question);
        q.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 12;");
        q.setWrapText(true);

        Label a = new Label("A: " + answer);
        a.setStyle("-fx-text-fill: white; -fx-font-size: 12; -fx-font-weight: bold;");
        a.setWrapText(true);

        row.getChildren().addAll(q, a);
        conclusionContent.getChildren().add(row);
    }

    // ── startQueueTimers() ────────────────────────────────────
    private void startQueueTimers(ScheduleResult rr, ScheduleResult pq) {
        List<String> rrStates = buildQueueSequence(rr.getGanttBlocks());
        List<String> pqStates = buildQueueSequence(pq.getGanttBlocks());

        int[] rrIdx = {0}, pqIdx = {0};

        rrQueueTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (rrIdx[0] < rrStates.size()) {
                rrQueueLabel.setText("Queue: " + rrStates.get(rrIdx[0]++));
            } else {
                rrQueueLabel.setText("Queue: [ ]  — simulation complete");
                rrQueueTimer.stop();
            }
        }));
        rrQueueTimer.setCycleCount(Timeline.INDEFINITE);
        rrQueueTimer.play();

        pqQueueTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (pqIdx[0] < pqStates.size()) {
                pqQueueLabel.setText("Queue: " + pqStates.get(pqIdx[0]++));
            } else {
                pqQueueLabel.setText("Queue: [ ]  — simulation complete");
                pqQueueTimer.stop();
            }
        }));
        pqQueueTimer.setCycleCount(Timeline.INDEFINITE);
        pqQueueTimer.play();
    }

    private List<String> buildQueueSequence(List<GanttBlock> blocks) {
        List<String> states = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            GanttBlock cur = blocks.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append("[ Running: ").append(cur.getPid())
                    .append("  t=").append(cur.getStartTime())
                    .append("→").append(cur.getEndTime());

            if (i + 1 < blocks.size()) {
                sb.append("  |  Waiting: ");
                int limit = Math.min(i + 4, blocks.size());
                for (int j = i + 1; j < limit; j++) {
                    sb.append(blocks.get(j).getPid());
                    if (j < limit - 1) sb.append(", ");
                }
                if (limit < blocks.size()) sb.append(", ...");
            }
            sb.append(" ]");
            states.add(sb.toString());
        }
        return states;
    }

    // ── Helpers ───────────────────────────────────────────────
    private void stopTimers() {
        if (rrQueueTimer != null) rrQueueTimer.stop();
        if (pqQueueTimer != null) pqQueueTimer.stop();
    }

    private String fmt(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}