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

}