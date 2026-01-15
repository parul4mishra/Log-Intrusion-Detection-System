# TODO / Future Enhancements

The following improvements can be implemented in the existing design

---

## 1. Observability (Enhancements)

- Extend the existing traceId propagation with basic metrics
  (events processed, alerts raised per rule)
- Add structured error reporting for rule evaluation failures
- Expose simple operational metrics via Actuator

---

## 2. Detection Rules (Enhancements)

- Enhance brute-force detection to consider additional signals
  (e.g. action=LOGIN and result=FAILURE instead of status code only)
- Add one or two additional rules (e.g., repeated access forbidden, noisy IP)
- Enhance detection logic beyond simple pattern matching
  (e.g. anomaly-based signals, repeated failures, or query context)
- Make detection thresholds configurable via application properties
- Improve rule state cleanup for long-running executions

---

## 3. Log Parsing

- Support additional structured formats (JSON logs, ECS, Nginx, Apache)
- Persist malformed or unparseable log lines for offline inspection
- Support multi-line logs (stack traces)

---

## 5. Ingestion Improvements

- Basic incremental reading to avoid reprocessing already-ingested log lines
- Track last processed file position per log file
- Gracefully handle application restarts without duplicating alerts
- Improve resilience when new log files appear or existing files are updated

## 6. Alert Model & API

- Add severity levels to alerts (LOW / MEDIUM / HIGH)
- Support filtering alerts by severity and type
- Alert persistence (database or event store)
- Alert lifecycle management (OPEN / ACKNOWLEDGED / RESOLVED)

---

## 6. Testing

- Add unit tests for detection rules
