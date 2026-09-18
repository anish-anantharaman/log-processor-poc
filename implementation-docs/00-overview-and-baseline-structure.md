# Log Processing System — 00. Overview & Baseline Project Structure

> Read this file first. It sets up the vocabulary (API = menu, Solace = mailboxes, JSON = the note format) and shows the **empty skeleton** the project starts from, before any phase adds its own folders. Each subsequent phase file (`phase-1-...md` through `phase-6-...md`) shows only **what that phase adds** on top of this skeleton, so you can see the project literally grow, file by file.

---

## 1. Tech Stack

| Technology | What it is, in beginner terms |
|---|---|
| **Kotlin** | The programming language we write the code in |
| **Spring Boot** | A toolkit that makes building services ("kitchens") easier |
| **Solace PubSub+** | A post office with labeled mailboxes (**topics**) and pickup slots (**queues**). A **publisher** drops messages onto a topic; a **subscriber** binds a queue to that topic and picks messages up from the queue. Bad/invalid messages go to a special mailbox called a **DMQ** (Dead Message Queue) instead of being thrown away. |
| **Spring Integration** | A toolkit for building step-by-step processing pipelines (an assembly line) |
| **JSON** | The plain-text format every request and response is written in |

Every phase in this system is really just: **pick something up from one mailbox → process it → drop the result into another mailbox.**

---

## 2. Baseline Folder Structure (Before Phase 1)

This is a brand-new, empty Spring Boot + Kotlin project — nothing phase-specific has been added yet.

```text
log-processing-system/
├── build.gradle.kts                # Build config: dependencies, plugins
├── settings.gradle.kts             # Project name & module settings
├── gradle.properties
├── README.md
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/tarento/logprocessing/
    │   │       └── LogProcessingApplication.kt   # The single "start button" for the whole app
    │   └── resources/
    │       ├── application.yml                   # Central configuration file
    │       └── application-local.yml             # Local developer overrides
    └── test/
        └── kotlin/
            └── com/tarento/logprocessing/
```

**In plain English:**
- `LogProcessingApplication.kt` is the file you "press play" on to start the whole system.
- `application.yml` is where all settings live (Solace broker URL, VPN name, ports, feature toggles) — no settings are hard-coded inside the actual logic files.
- Every phase from here on **only adds new folders/files under `com/tarento/logprocessing/`** — the outer skeleton (`build.gradle.kts`, `settings.gradle.kts`, etc.) never changes.

---

## 3. How to Read the Phase Files

Each of the six phase files follows the same structure:

1. **Goal** — one sentence, plain English
2. **Functional Requirements** — the checklist for that phase
3. **API Contract** — the exact JSON request/response shape, with a plain-English translation
4. **Folder Structure Evolution** — a `git diff`-style view: what's **🆕 new** in this phase, layered on top of everything before it
5. **Flow Diagram** — the story of one message moving through this phase

Read them in order: **Phase 1 → 2 → 3 → 4 → 5 → 6**. Each phase's output becomes the next phase's input, and each phase's code sits *next to*, not *on top of*, the previous phase's code — nothing gets deleted or rewritten as the system grows.

---

## 4. Solace Terminology Quick Reference

| Term | Meaning |
|---|---|
| **Topic** | A named subject a message is published under (e.g. `logs/raw`); hierarchical, e.g. `logs/level/error` |
| **Queue** | A physical mailbox that stores messages until a subscriber picks them up; a queue is *bound* to one or more topics |
| **Publisher** | Something that sends a message onto a topic |
| **Subscriber** | Something that consumes messages from a queue |
| **DMQ (Dead Message Queue)** | Where invalid/undeliverable messages go instead of being lost |
| **Queue backlog / spool depth** | How many unread messages are waiting in a queue |

---

## 5. File Index

| File | Covers |
|---|---|
| `00-overview-and-baseline-structure.md` | This file — concepts, tech stack, empty skeleton |
| `phase-1-ingestion.md` | Solace consume → parse → normalize → DMQ → publish |
| `phase-2-enrichment.md` | Adding metadata via Spring Integration |
| `phase-3-filtering-routing.md` | Rule-based routing to multiple topics |
| `phase-4-storage.md` | OpenSearch + S3 persistence |
| `phase-5-aggregation-alerting.md` | Counting logs + threshold alerts |
| `phase-6-observability-hardening.md` | Actuator, metrics, tracing, security |
