# Phase 1 — Basic Log Ingestion & Processing

*(Builds on: `00-overview-and-baseline-structure.md`)*

## 1. Goal

Take a messy raw log, check if it's valid, and turn it into a clean, standard format everyone downstream can rely on.

## 2. Functional Requirements

- Consume logs from a Solace queue bound to the `logs/raw` topic (e.g. queue `raw-logs.queue`).
- Support JSON logs initially.
- Parse the incoming log.
- Convert it into a common `LogEvent` format.
- Generate a unique `eventId`.
- Add ingestion timestamp.
- Store the original raw log.
- Send invalid logs to a DMQ (Dead Message Queue).
- Publish successfully processed logs to a Solace `logs/processed` topic.

## 3. API Contract

**This phase listens to:** `raw-logs.queue` (bound to topic `logs/raw`)
**Success output goes to:** `logs/processed` topic
**Failure output goes to:** `raw-logs.dmq` (Dead Message Queue)

### 3.1 REQUEST — Raw Incoming Log

```json
{
  "service": "checkout-service",
  "level": "ERROR",
  "message": "Payment gateway timeout after 30s",
  "timestamp": "2026-09-11T10:15:30Z"
}
```

| Field | Type | Required? | Notes |
|---|---|---|---|
| `service` | string | Yes | Name of the application that generated the log |
| `level` | string | Yes | One of: `DEBUG`, `INFO`, `WARN`, `ERROR` |
| `message` | string | Yes | The actual log message |
| `timestamp` | date-time string | Yes | When the log was originally generated |

### 3.2 RESPONSE (Success) — Canonical `LogEvent`

```json
{
  "eventId": "a1b2c3d4-0000-1111-2222-333344445555",
  "service": "checkout-service",
  "level": "ERROR",
  "message": "Payment gateway timeout after 30s",
  "originalTimestamp": "2026-09-11T10:15:30Z",
  "ingestionTimestamp": "2026-09-11T10:15:31Z",
  "rawLog": "{\"service\":\"checkout-service\",\"level\":\"ERROR\",...}"
}
```

### 3.3 RESPONSE (Failure) — DMQ Message

```json
{
  "originalPayload": "{\"service\":\"checkout-service\",\"level\":\"CRITICAL\",\"message\":null}",
  "failureReason": "Invalid 'level' value: CRITICAL is not one of [DEBUG, INFO, WARN, ERROR]. Also 'message' is missing.",
  "failedAtStage": "PARSE_AND_VALIDATE",
  "failureTimestamp": "2026-09-11T10:15:32Z"
}
```

## 4. Flow Diagram

```text
Solace (raw-logs.queue, bound to logs/raw)
  ↓
Raw Log Subscriber
  ↓
Parse JSON  ──(fails)──→ DMQ (raw-logs.dmq)
  ↓ (succeeds)
Validate against contract  ──(fails)──→ DMQ (raw-logs.dmq)
  ↓ (succeeds)
Build canonical LogEvent (add eventId + ingestionTimestamp)
  ↓
Publish to Solace topic (logs/processed)
```

## 5. Folder Structure Evolution

Everything marked **🆕** is new in this phase. Nothing from the baseline skeleton is changed or removed.

```text
log-processing-system/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/tarento/logprocessing/
    │   │       ├── LogProcessingApplication.kt
    │   │       ├── model/                                    🆕
    │   │       │   ├── RawLog.kt                              🆕  (Section 3.1 contract)
    │   │       │   ├── LogEvent.kt                             🆕  (Section 3.2 contract)
    │   │       │   └── DmqMessage.kt                            🆕  (Section 3.3 contract)
    │   │       ├── ingestion/                                🆕
    │   │       │   ├── subscriber/
    │   │       │   │   └── RawLogSubscriber.kt               🆕  (binds to raw-logs.queue)
    │   │       │   ├── parser/
    │   │       │   │   └── RawLogParser.kt                   🆕  (JSON parsing + validation)
    │   │       │   ├── mapper/
    │   │       │   │   └── LogEventMapper.kt                 🆕  (RawLog → LogEvent)
    │   │       │   └── dmq/
    │   │       │       └── DmqPublisher.kt                   🆕  (publishes to raw-logs.dmq)
    │   │       └── solace/                                   🆕
    │   │           ├── config/
    │   │           │   └── SolaceConfig.kt                   🆕  (broker URL, VPN, topic/queue names)
    │   │           └── publisher/
    │   │               └── ProcessedLogPublisher.kt          🆕  (publishes to logs/processed)
    │   └── resources/
    │       ├── application.yml                               ✏️ updated (Solace host, VPN, topic/queue names added)
    │       └── application-local.yml
    └── test/
        └── kotlin/
            └── com/tarento/logprocessing/
                └── ingestion/                                🆕
                    ├── RawLogParserTest.kt                    🆕
                    └── LogEventMapperTest.kt                  🆕
```

**Legend:** 🆕 = new file/folder in this phase · ✏️ = existing file modified, not replaced.
