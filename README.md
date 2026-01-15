
# Log-Based Intrusion Detection & Alerting System

## Building the Docker Image

### Option 1: Using Docker directly
```bash
docker build -t intrusion-detector .

```

### Option 2: Using Docker Compose
```bash
docker-compose build
```
## Running the Application

Prepare logs directory and add the logs
```bash
mkdir logs
```
```bash
touch logs/application.log
```
The application reads and processes all log files present in `/logs` during startup.
Detection rules are applied once at application startup by processing all log files present in /logs.

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

 ## Design Choices

The system is designed as a modular pipeline, allowing each stage to evolve independently:

- **Single Responsibility**
    Each stage has a single responsibility and is decoupled from the others.  
- **Log Sources**
    The ingestion layer abstracts log sources via a `LogSource` interface.
    New sources in future, can be added without impacting parsing or detection.

- **Log Parsing**
   Parsing is handled by pluggable `LogParser` implementations.
   Multiple log formats from different services  can be supported by adding new parsers.

- **Detection Rules**
  Detection logic is implemented as independent rules following a common contract.
  New attack types can be added without modifying existing rules.

- **Alert Model**
  Alerts use a generic `actor / target` structure, making them reusable across services and attack types.
  
- **Single-pass log file processing**
   Logs are processed sequentially from a mounted file on application startup.
   This ensures deterministic behavior, simplicity.

 This design allows the system to start simple while supporting incremental complexity as requirements evolve.

## Trade-offs

- **Startup-time processing**
  Logs are analyzed on application startup rather than streamed in real time.
  This simplifies the design but delays detection until logs are processed.
  
- **File-based ingestion**
  Simple and dependency-free, but not intended for high-volume
  or real-time log processing.

- **Simple pattern-based rules vs deep parsing**
  Detection rules rely on structured fields and lightweight pattern checks,
  favoring clarity over exhaustive attack detection.

## Observability & Debugging 

- Structured logs with consistent prefixes (log.ingestion.*, parser.parse.*, rule.evaluation.*) to enable log-based debugging.
- Trace ID propagation across ingestion → rule evaluation → alert generation for correlating events.
- Failures are explicitly logged and never halt the pipeline, ensuring graceful degradation and easy debugging when
  log formats evolve or unexpected input is encountered.   
                             



*Note: An LLM was used as a productivity aid for refining documentation
and validating design ideas.*



