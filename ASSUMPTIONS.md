# Assumptions

This document lists the assumptions made while designing and implementing the Log-Based  Intrusion Detection & Alerting system.

---

## 1. Log Source & Ingestion

- Logs are read from a **mounted directory** (`/logs`). 
- Log ingestion starts automatically when the application boots.
- Logs are processed in **batch mode** (read line-by-line from files) rather than real-time streaming.
- Log files are assumed to be **append-only** during runtime.

---

## 2. Log Format Assumptions

- Logs follow a **structured key-value format** within a single line.
- Each log entry is assumed to be **self-contained in one line**.
- Sample expected format:
- **2026-01-14T12:30:05Z ERROR auth-service event=AUTH_FAILURE statusCode=401 ip=192.168.1.10 message="Login failed" **
- Fields such as `event`, `statusCode`, `ip`, and `service` are assumed to be present for detection rules to work reliably.
- Free-text `message` fields are not used for detection logic, only scoped for human readability.

---

## 3. Detection Rules Assumptions

- Detection rules operate **independently** and are evaluated sequentially for each log event.
- Rules are **stateless across restarts** (no persistence of counters or history).
- Time-based rules (e.g. brute-force detection) rely on timestamps present in logs and are evaluated in-memory.
- Rules are designed to be **simple and deterministic**.

---

## 4. Brute Force Authentication Rule

- Brute force detection is based on:
  - `event = AUTH_FAILURE`
  - `statusCode = 401`
- Detection is scoped by **IP address**.
- A fixed threshold and time window is assumed (e.g. *N failures within T seconds*) to minimize false positives.
- Successful logins reset or stop further brute-force escalation for that IP .

---

## 5. SQL Injection Rule

- SQL Injection detection is **pattern-based**.
- The presence of suspicious SQL patterns (e.g. `OR 1=1`, `'--`, `UNION SELECT`) in query fields is assumed to indicate an attack.
- False positives are acceptable for this simplified implementation.
- Logs are assumed to include the raw SQL query when database errors occur.

---

## 6. Alert Model Assumptions

- Alerts follow a **generic and extensible structure**:

```json
{
  "type": "BRUTE_FORCE_AUTH",
  "actor": {
    "type": "IP",
    "value": "192.168.1.10"
  },
  "target": {
    "service": "auth-service"
  },
  "description": "5 failed login attempts within 60 seconds",
  "timestamp": 1768453815475
}
```
## 7. API & Alert Retrieval

- Alerts are stored in-memory for simplicity.
- /alerts endpoint returns all alerts regardless of type.
- Alerts are not persisted across application restarts.


> Note: While certain formats and behaviors are assumed for this assignment,
> the system is intentionally designed to support multiple log formats, sources,
> and detection rules as it evolves.

