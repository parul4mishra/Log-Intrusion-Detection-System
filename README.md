# Log-Based Intrusion Detection & Alerting System

# Docker Setup for Log Alert System

## Building the Docker Image

### Option 1: Using Docker directly
```bash
docker build -t log-alert-system:latest .
```

### Option 2: Using Docker Compose
```bash
docker-compose build
```
## Running the Application

Prepare logs directory
1.Create a logs folder next to the project:
```bash
mkdir logs
```
```bash
touch logs/application.log
```
 The application reads and processes all log files present in `/logs` during startup.
- Detection rules are applied once when the application starts.
- For simplicity, real-time log tailing is not implemented.

To trigger detection:
1. Populate `logs/application.log`
2. Start (or restart) the container
3. View detected alerts via curl http://localhost:8080/alerts

- A `sample-logs/` folder is included in the repository to demonstrate the expected log format.

### Option 1 :Run the application
```bash
docker run -p 8080:8080 -v $(pwd)/logs:/logs intrusion-detector

```
### Option 2: Using Docker Compose (Recommended)
```bash
docker-compose up -d
```

## Accessing the Application

Once running, the application will be available at:
- Application: http://localhost:8080
- Logs are read from /logs/application.log

## Architecture Design & Reasoning

This project is designed as a log-based intrusion detection system  that processes application logs, applies security detection rules, and exposes detected alerts over HTTP.
The architecture follows a pipeline-based, modular design:

LogSource → LogParser → DetectionService → DetectionRules → AlertService → REST API
Each stage has a single responsibility and can be independently extended or replaced.

- ## Design Choices

- **Rule-based detection engine**
  Detection logic is implemented as independent rules (e.g., brute-force, SQL injection),
  allowing new attack patterns to be added without changing the core pipeline.

- **Structured alert model (actor / target)**
  Alerts explicitly separate the attacker (actor, e.g., IP) from the affected system
  (target, e.g., service), making the model generic across different log sources.

- **Single-pass log file processing**
  Logs are processed sequentially from a mounted file on application startup.
  This ensures deterministic behavior, simplicity, and avoids state synchronization issues.


## Trade-offs

- **Startup-time processing vs real-time detection**
  Logs are analyzed on application startup rather than streamed in real time.
  This simplifies the design but delays detection until logs are processed.

- **File-based ingestion vs log pipelines**
  Reading from a mounted log file avoids external dependencies but does not scale
  as well as Kafka / Fluentd–based ingestion.

- **Simple pattern-based rules vs deep parsing**
  Detection rules rely on structured fields and lightweight pattern checks,
  favoring clarity over exhaustive attack detection.

## Observability & Debugging 

Structured logs with consistent prefixes (log.ingestion.*, parser.parse.*, rule.evaluation.*) to enable log-based debugging.
Trace ID propagation across ingestion → rule evaluation → alert generation for correlating events.

                             






