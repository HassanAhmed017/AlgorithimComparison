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
    @FXML
    private TextField tfPid, tfArrival,
            tfBurst, tfPriority, tfQuantum;

    // ── Process Table ─────────────────────────────────────────
    @FXML
    private TableView<Process> processTable;
    @FXML
    private TableColumn<Process, Integer> colPid,
            colArrival,
            colBurst,
            colPriority;

    // ── Round Robin ───────────────────────────────────────────
    @FXML
    private Label rrQueueLabel;
    @FXML
    private Pane rrGanttPane;
    @FXML
    private ScrollPane rrGanttScroll;
    @FXML
    private Label rrAvgLabel;
    @FXML
    private TableView<String[]> rrResultTable;
    @FXML
    private TableColumn<String[], String> rrColPid,
            rrColFt,
            rrColTat,
            rrColWt,
            rrColRt;

    // ── Priority ──────────────────────────────────────────────
    @FXML
    private Label pqQueueLabel;
    @FXML
    private Pane pqGanttPane;
    @FXML
    private ScrollPane pqGanttScroll;
    @FXML
    private Label pqAvgLabel;
    @FXML
    private TableView<String[]> pqResultTable;
    @FXML
    private TableColumn<String[], String> pqColPid,
            pqColFt,
            pqColTat,
            pqColWt,
            pqColRt;

    // ── Comparison Table Labels ───────────────────────────────
    @FXML
    private Label rrAvgWtLabel, rrAvgTatLabel,
            rrAvgRtLabel;
    @FXML
    private Label pqAvgWtLabel, pqAvgTatLabel,
            pqAvgRtLabel;

    // ── Left Result Panel ─────────────────────────────────────
    @FXML
    private Label bestAlgoLabel;
    @FXML
    private Label scenarioLabel;
    @FXML
    private Label starvationLabel;

    // ── Conclusion ────────────────────────────────────────────
    @FXML
    private VBox conclusionContent;
    @FXML
    private TextArea conclusionText;

    // ── Internal State ────────────────────────────────────────
    private final ObservableList<Process> processList =
            FXCollections.observableArrayList();

    private Timeline rrQueueTimer, pqQueueTimer;

    private static final String[] COLORS = {
            "#5B8DB8", "#6BAA75", "#C0695A", "#C09A5A",
            "#8B7BAA", "#5AAAB5", "#AA7B8B", "#5AB5AA",
            "#AA9B5A", "#8BAA5A"
    };

    // ── initialize() ─────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Process input table
        // NOTE: uses "brustTime" to match Process.java getter
        colPid.setCellValueFactory(
                new PropertyValueFactory<>("pid"));
        colArrival.setCellValueFactory(
                new PropertyValueFactory<>("arrivalTime"));
        colBurst.setCellValueFactory(
                new PropertyValueFactory<>("brustTime"));
        colPriority.setCellValueFactory(
                new PropertyValueFactory<>("priority"));
        processTable.setItems(processList);

        // RR result table columns
        // Array index: 0=PID, 1=FT, 2=TAT, 3=WT, 4=RT
        rrColPid.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[0]));
        rrColFt.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[1]));
        rrColTat.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[2]));
        rrColWt.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[3]));
        rrColRt.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[4]));

        // Priority result table columns
        // Array index: 0=PID, 1=FT, 2=TAT, 3=WT, 4=RT
        pqColPid.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[0]));
        pqColFt.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[1]));
        pqColTat.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[2]));
        pqColWt.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[3]));
        pqColRt.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue()[4]));
    }

    // ── Handle Add Process ────────────────────────────────────
    @FXML
    private void handleAddProcess() {
        try {
            int pid = Integer.parseInt(
                    tfPid.getText().trim());
            int arrival = Integer.parseInt(
                    tfArrival.getText().trim());
            int burst = Integer.parseInt(
                    tfBurst.getText().trim());
            int priority = Integer.parseInt(
                    tfPriority.getText().trim());

            Process p = new Process(
                    pid, arrival, burst, priority);


            InputValidator.validateProcess(p, processList);

            processList.add(p);

            tfPid.clear();
            tfArrival.clear();
            tfBurst.clear();
            tfPriority.clear();

        } catch (NumberFormatException e) {
            showError("All fields must be valid integers.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // ── Handle Remove ─────────────────────────────────────────
    @FXML
    private void handleRemoveProcess() {
        Process sel = processTable
                .getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Please select a process to remove.");
            return;
        }
        processList.remove(sel);
    }

    // ── Handle Clear ──────────────────────────────────────────
    @FXML
    private void handleClear() {
        processList.clear();

        rrGanttPane.getChildren().clear();
        pqGanttPane.getChildren().clear();
        rrResultTable.getItems().clear();
        pqResultTable.getItems().clear();

        rrQueueLabel.setText("Queue: [ ]");
        pqQueueLabel.setText("Queue: [ ]");
        rrAvgLabel.setText(
                "Avg WT: —  |  Avg TAT: —  |  Avg RT: —");
        pqAvgLabel.setText(
                "Avg WT: —  |  Avg TAT: —  |  Avg RT: —");

        conclusionContent.getChildren().clear();
        conclusionText.setText(
                "Run the simulation to generate the conclusion.");

        // Reset all dynamic labels
        List.of(rrAvgWtLabel, rrAvgTatLabel, rrAvgRtLabel,
                        pqAvgWtLabel, pqAvgTatLabel, pqAvgRtLabel,
                        bestAlgoLabel, scenarioLabel, starvationLabel)
                .forEach(l -> l.setText("—"));

        stopTimers();
    }

    // ── Handle Run ────────────────────────────────────────────
    @FXML
    private void handleRun() {
        try {
            int quantum = Integer.parseInt(
                    tfQuantum.getText().trim());
            InputValidator.validateQuantum(quantum);

            // Sync processList → Scheduler before running
            Scheduler.quantum = quantum;
            Scheduler.processes = new ArrayList<>(processList);

            stopTimers();

            SimulationResult result = Scheduler.start();
            if (result == null) {
                showError(
                        "Simulation failed. Check your input.");
                return;
            }

            displayRRResults(result.getRrResult());
            displayPQResults(result.getPqResult());
            updateComparisonLabels(
                    result.getRrResult(), result.getPqResult());
            updateLeftPanel(
                    result.getRrResult(), result.getPqResult());
            displayConclusion(
                    result.getRrResult(), result.getPqResult());
            startQueueTimers(
                    result.getRrResult(), result.getPqResult());

        } catch (NumberFormatException e) {
            showError("Quantum must be a valid integer.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // ── Display RR Results ────────────────────────────────────
    private void displayRRResults(ScheduleResult result) {

        // RR Gantt blocks already have "P1","P2" format
        // isRR=true tells drawGanttChart not to add P prefix
        drawGanttChart(rrGanttPane,
                result.getGanttBlocks(), true);

        fillResultsTable(rrResultTable, result);

        rrAvgLabel.setText(String.format(Locale.US,
                "Avg WT: %.2f  |  Avg TAT: %.2f  |  Avg RT: %.2f",
                result.getAvgWaitingTime(),
                result.getAvgTurnaroundTime(),
                result.getAvgResponseTime()
        ));
    }

    // ── Display Priority Results ─────
    private void displayPQResults(ScheduleResult result) {

        // Priority Gantt blocks have "P1","P2" format
        drawGanttChart(pqGanttPane,
                result.getGanttBlocks(), true);

        fillResultsTable(pqResultTable, result);

        pqAvgLabel.setText(String.format(Locale.US,
                "Avg WT: %.2f  |  Avg TAT: %.2f  |  Avg RT: %.2f",
                result.getAvgWaitingTime(),
                result.getAvgTurnaroundTime(),
                result.getAvgResponseTime()
        ));
    }

    private void drawGanttChart(
            Pane pane,
            List<GanttBlock> blocks,
            boolean isRR) {

        pane.getChildren().clear();
        if (blocks == null || blocks.isEmpty()) return;

        double scale = 50.0;
        double blockH = 50.0;
        double labelY = blockH + 18;
        double totalWidth =
                blocks.get(blocks.size() - 1).getEndTime()
                        * scale + 60;

        pane.setPrefSize(totalWidth, blockH + 30);

        for (GanttBlock block : blocks) {
            double x = block.getStartTime() * scale;
            double w = block.getDuration() * scale;

            // Both algos store pid as "P1" or "IDLE"
            String displayPid = block.getPid();

            String colorHex =
                    block.getPid().equals("IDLE")
                            ? "#4A5568"
                            : COLORS[Math.abs(
                            block.getPid().hashCode())
                                     % COLORS.length];

            Rectangle rect = new Rectangle(
                    x, 0, Math.max(w - 2, 1), blockH);
            rect.setFill(Color.web(colorHex, 0.9));
            rect.setStroke(Color.web(colorHex));
            rect.setStrokeWidth(1.5);
            rect.setArcWidth(6);
            rect.setArcHeight(6);

            Text pidText = new Text(displayPid);
            pidText.setFont(Font.font(
                    "System", FontWeight.BOLD, 12));
            pidText.setFill(Color.WHITE);
            pidText.setX(
                    x + w / 2 - displayPid.length() * 4);
            pidText.setY(blockH / 2 + 5);

            Text startText = new Text(
                    String.valueOf(block.getStartTime()));
            startText.setFont(Font.font("Monospace", 10));
            startText.setFill(Color.web("#A8C4C4"));
            startText.setX(x);
            startText.setY(labelY);

            pane.getChildren().addAll(
                    rect, pidText, startText);
        }

        GanttBlock last =
                blocks.get(blocks.size() - 1);
        Text endText = new Text(
                String.valueOf(last.getEndTime()));
        endText.setFont(Font.font("Monospace", 10));
        endText.setFill(Color.web("#A8C4C4"));
        endText.setX(last.getEndTime() * scale);
        endText.setY(labelY);
        pane.getChildren().add(endText);
    }

    private void fillResultsTable(
            TableView<String[]> table,
            ScheduleResult result) {

        ObservableList<String[]> rows =
                FXCollections.observableArrayList();

        for (Process p : result.getProcesses()) {
            int rt = p.getResponseTime() - p.getArrivalTime();

            rows.add(new String[]{
                    "P" + p.getPid(),
                    String.valueOf(p.getFinishTime()),
                    String.valueOf(p.getTurnaroundTime()),
                    String.valueOf(p.getWaitingTime()),
                    String.valueOf(rt)
            });
        }

        // Average row at the bottom
        rows.add(new String[]{
                "AVG",
                "—",
                fmt(result.getAvgTurnaroundTime()),
                fmt(result.getAvgWaitingTime()),
                fmt(result.getAvgResponseTime())
        });

        table.setItems(rows);
    }

    // ── Update Comparison Table Labels ───────────────────────
    private void updateComparisonLabels(
            ScheduleResult rr, ScheduleResult pq) {

        rrAvgWtLabel.setText(fmt(rr.getAvgWaitingTime()));
        rrAvgTatLabel.setText(fmt(rr.getAvgTurnaroundTime()));
        rrAvgRtLabel.setText(fmt(rr.getAvgResponseTime()));
        pqAvgWtLabel.setText(fmt(pq.getAvgWaitingTime()));
        pqAvgTatLabel.setText(fmt(pq.getAvgTurnaroundTime()));
        pqAvgRtLabel.setText(fmt(pq.getAvgResponseTime()));

        // Highlight better (lower) value in green
        highlightBetter(rrAvgWtLabel, pqAvgWtLabel,
                rr.getAvgWaitingTime(),
                pq.getAvgWaitingTime());
        highlightBetter(rrAvgTatLabel, pqAvgTatLabel,
                rr.getAvgTurnaroundTime(),
                pq.getAvgTurnaroundTime());
        highlightBetter(rrAvgRtLabel, pqAvgRtLabel,
                rr.getAvgResponseTime(),
                pq.getAvgResponseTime());
    }

    // Highlights the label with the lower value in green
    private void highlightBetter(
            Label rrLabel, Label pqLabel,
            double rrVal, double pqVal) {

        String base =
                "-fx-background-color: #1C3333;"
                        + "-fx-border-color: #4A7C7C;"
                        + "-fx-border-width: 1;"
                        + "-fx-alignment: CENTER;"
                        + "-fx-padding: 7 10 7 10;";

        String win =
                "-fx-background-color: #1A3A1A;"
                        + "-fx-border-color: #4CAF50;"
                        + "-fx-border-width: 2;"
                        + "-fx-alignment: CENTER;"
                        + "-fx-padding: 7 10 7 10;";

        if (rrVal < pqVal) {
            rrLabel.setStyle(win
                    + "-fx-text-fill: #4CAF50;"
                    + "-fx-font-weight: bold;");
            pqLabel.setStyle(base
                    + "-fx-text-fill: white;");
        } else if (pqVal < rrVal) {
            pqLabel.setStyle(win
                    + "-fx-text-fill: #4CAF50;"
                    + "-fx-font-weight: bold;");
            rrLabel.setStyle(base
                    + "-fx-text-fill: white;");
        } else {
            // Tie
            rrLabel.setStyle(base
                    + "-fx-text-fill: #FFD700;");
            pqLabel.setStyle(base
                    + "-fx-text-fill: #FFD700;");
        }
    }

    // ── Update Left Panel ─────────────────────────────────────
    private void updateLeftPanel(
            ScheduleResult rr, ScheduleResult pq) {

        // ── Winner ────────────────────────────────────────────
        double rrScore = rr.getAvgWaitingTime()
                + rr.getAvgTurnaroundTime();
        double pqScore = pq.getAvgWaitingTime()
                + pq.getAvgTurnaroundTime();

        if (rrScore < pqScore) {
            bestAlgoLabel.setText("Round Robin ✓");
            bestAlgoLabel.setStyle(
                    "-fx-font-size: 13px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: #4CAF50;");
        } else if (pqScore < rrScore) {
            bestAlgoLabel.setText("Priority Scheduling ✓");
            bestAlgoLabel.setStyle(
                    "-fx-font-size: 13px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: #FF6B6B;");
        } else {
            bestAlgoLabel.setText("Tie");
            bestAlgoLabel.setStyle(
                    "-fx-font-size: 13px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: #FFD700;");
        }

        // ── Scenario Detection ────────────────────────────────
//        scenarioLabel.setText(
//                detectScenario(pq));

        // ── Starvation Detection ──────────────────────────────
        double maxWt = pq.getProcesses().stream()
                .mapToInt(Process::getWaitingTime)
                .max().orElse(0);
        boolean starvationRisk =
                maxWt > pq.getAvgWaitingTime() * 2
                        && pq.getProcesses().size() > 1;

        if (starvationRisk) {
            starvationLabel.setText(
                    " Risk Detected\nin Priority");
            starvationLabel.setStyle(
                    "-fx-font-size: 11px;"
                            + "-fx-text-fill: #FF6B6B;");
        } else {
            starvationLabel.setText(
                    " No Starvation");
            starvationLabel.setStyle(
                    "-fx-font-size: 11px;"
                            + "-fx-text-fill: #4CAF50;");
        }
    }

    // ── Display Conclusion ────────────────────────────────────
    private void displayConclusion(
            ScheduleResult rr, ScheduleResult pq) {

        conclusionContent.getChildren().clear();

        String[][] rows = {
                {
                        "Which algorithm gave better avg waiting time?",
                        rr.getAvgWaitingTime() <= pq.getAvgWaitingTime()
                                ? "Round Robin  (WT = "
                                  + fmt(rr.getAvgWaitingTime()) + ")"
                                : "Priority Scheduling  (WT = "
                                  + fmt(pq.getAvgWaitingTime()) + ")"
                },
                {
                        "Which algorithm gave better response time?",
                        rr.getAvgResponseTime()
                                <= pq.getAvgResponseTime()
                                ? "Round Robin  (RT = "
                                  + fmt(rr.getAvgResponseTime()) + ")"
                                : "Priority Scheduling  (RT = "
                                  + fmt(pq.getAvgResponseTime()) + ")"
                },
                {
                        "Did higher-priority processes gain advantage?",
                        "Yes  Priority Scheduling served "
                                + "high-priority processes first."
                },
                {
                        "Did Round Robin appear more balanced?",
                        "Yes  Round Robin distributed CPU time "
                                + "evenly across all processes."
                },
                {
                        "Was starvation observed in Priority?",
                        "Possible  low-priority processes may "
                                + "wait significantly longer."
                },
                {
                        "Which algorithm is recommended?",
                        rr.getAvgWaitingTime() <= pq.getAvgWaitingTime()
                                ? "Round Robin — better overall waiting "
                                  + "time and fairness."
                                : "Priority Scheduling — better for "
                                  + "urgent task treatment."
                }
        };

        for (String[] row : rows)
            addAnalysisRow(row[0], row[1]);

        conclusionText.setText(String.join("\n\n",
                "CONCLUSION",
                rr.getAvgWaitingTime() < pq.getAvgWaitingTime()
                        ? "Round Robin achieved lower average waiting"
                          + " time (" + fmt(rr.getAvgWaitingTime())
                          + ") compared to Priority Scheduling ("
                          + fmt(pq.getAvgWaitingTime()) + ")."
                        : "Priority Scheduling achieved lower average"
                          + " waiting time ("
                          + fmt(pq.getAvgWaitingTime())
                          + ") compared to Round Robin ("
                          + fmt(rr.getAvgWaitingTime()) + ").",
                "Priority-based service improved urgent-task "
                        + "treatment by ensuring high-priority processes "
                        + "received CPU time first.",
                "Round Robin improved fairness by distributing "
                        + "CPU time equally across all processes "
                        + "regardless of priority.",
                "Starvation risk exists in Priority Scheduling "
                        + "when low-priority processes are continuously "
                        + "preempted by arriving high-priority processes.",
                "Round Robin is recommended for interactive "
                        + "and time-sharing systems where fairness matters."
                        + " Priority Scheduling is recommended for systems"
                        + " where task urgency must be respected."
        ));
    }

    // ── Add One Analysis Row ──────────────────────────────────
    private void addAnalysisRow(
            String question, String answer) {

        VBox row = new VBox(4);
        row.setStyle(
                "-fx-background-color: #1C3333;"
                        + "-fx-border-color: #4A7C7C;"
                        + "-fx-border-radius: 4;"
                        + "-fx-background-radius: 4;"
        );
        row.setPadding(new Insets(8));

        Label q = new Label("Q: " + question);
        q.setStyle(
                "-fx-text-fill: #FFD700;"
                        + "-fx-font-size: 12;");
        q.setWrapText(true);

        Label a = new Label("A: " + answer);
        a.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 12;"
                        + "-fx-font-weight: bold;");
        a.setWrapText(true);

        row.getChildren().addAll(q, a);
        conclusionContent.getChildren().add(row);
    }

    // ── Start Queue Timers ────────────────────────────────────
    private void startQueueTimers(
            ScheduleResult rr, ScheduleResult pq) {

        List<String> rrQueue =
                buildQueueSequence(rr.getGanttBlocks());
        List<String> pqQueue =
                buildQueueSequence(pq.getGanttBlocks());

        int[] rrIndex = {0}, pqIndex = {0};

        rrQueueTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (rrIndex[0] < rrQueue.size()) {
                        rrQueueLabel.setText(
                                "Queue: "
                                        + rrQueue.get(rrIndex[0]++));
                    } else {
                        rrQueueLabel.setText(
                                "Queue: [ ]  — complete");
                        rrQueueTimer.stop();
                    }
                })
        );
        rrQueueTimer.setCycleCount(Timeline.INDEFINITE);
        rrQueueTimer.play();

        pqQueueTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (pqIndex[0] < pqQueue.size()) {
                        pqQueueLabel.setText(
                                "Queue: "
                                        + pqQueue.get(pqIndex[0]++));
                    } else {
                        pqQueueLabel.setText(
                                "Queue: [ ]  — complete");
                        pqQueueTimer.stop();
                    }
                })
        );
        pqQueueTimer.setCycleCount(Timeline.INDEFINITE);
        pqQueueTimer.play();
    }

    // ── Build Queue Sequence ──────────────────────────────────
    // Both algorithms store PIDs as "P1","P2" already
    private List<String> buildQueueSequence(
            List<GanttBlock> blocks) {

        List<String> sequence = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            GanttBlock current = blocks.get(i);
            StringBuilder sb = new StringBuilder();

            sb.append("[ Running: ")
                    .append(current.getPid())
                    .append(" (t=")
                    .append(current.getStartTime())
                    .append("→")
                    .append(current.getEndTime())
                    .append(")");

            if (i + 1 < blocks.size()) {
                sb.append("  |  Waiting: ");
                int limit =
                        Math.min(i + 4, blocks.size());
                for (int j = i + 1; j < limit; j++) {
                    sb.append(blocks.get(j).getPid());
                    if (j < limit - 1) sb.append(", ");
                }
                if (limit < blocks.size())
                    sb.append(", ...");
            }

            sb.append(" ]");
            sequence.add(sb.toString());
        }

        return sequence;
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