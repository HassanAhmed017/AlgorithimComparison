# Algorithm Comparison

A Java desktop application that compares two CPU scheduling algorithms, **Round Robin** and **Preemptive Priority Scheduling**, by running them on the same set of processes and presenting the results side by side.

## Table of Contents

- [About](#about)
- [Features](#features)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [License](#license)

## About

This project simulates and compares the behavior of two well-known CPU scheduling algorithms:

| Algorithm | Description |
|-----------|-------------|
| **Round Robin (RR)**    | A preemptive algorithm that assigns a fixed time quantum to each process in a circular order. |
| **Preemptive Priority** | Selects the highest-priority process at each time unit, using Round Robin as a tiebreaker among processes with equal priority. |

Both algorithms are executed on an identical copy of the process list. The application then reports per-process metrics and averages, allowing direct comparison of scheduling performance.

## Features

- Create processes with custom ID, arrival time, burst time, and priority
- Configure the time quantum used by both algorithms
- Run both algorithms simultaneously on identical process sets
- Generate Gantt chart timelines for each algorithm
- Calculate and compare average waiting time, turnaround time, and response time
- Input validation for process attributes and quantum values
- analyze which algorithm is better

## Project Structure

```
src/main/java/
├── algorithm/
│   ├── Scheduler.java            # Main controller and process management
│   ├── RoundRobin.java           # Round Robin implementation
│   └── PriorityScheduler.java    # Preemptive Priority implementation
├── model/
│   ├── Process.java              # Process data model
│   ├── GanttBlock.java           # Gantt chart block representation
│   ├── ScheduleResult.java       # Single algorithm result (Gantt + averages)
│   ├── SimulationResult.java     # Combined result for both algorithms
|   └── Resource.java             # Resource for process 
├── validation/
│   └── InputValidator.java       # Input validation logic
└── ui/
    ├── mainApp.java              # JavaFX application entry point
    └── mainController.java       # JavaFX UI controller
```

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 21    |
| UI Framework | JavaFX 21 |
| Build Tool | Maven |

## Getting Started

1. **Clone the repository**

```bash
git https://github.com/HassanAhmed017/AlgorithimComparison.git


2. **Build the project**

```bash
mvn clean compile
```

3. **Run the application**

```bash
mvn javafx:run
```

You can also run `Scheduler.main()` directly for a console-based interface.

## Usage

1. Add processes by specifying an ID, arrival time, burst time, and priority.
2. Set the time quantum.
3. Start the simulation.
4. The application runs both Round Robin and Preemptive Priority on the same process data.
5. Review the Gantt chart and average metrics (waiting time, turnaround time, response time) for each algorithm.

## License

This project is for educational purposes.
