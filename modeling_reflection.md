# modeling_reflection.md — Reflection on Object State and Activity Workflow Modeling
## Hospital Patient Monitoring System (HPMS)

---

## Reflection: Lessons Learned in State and Activity Diagram Modeling

### Choosing the Right Level of Granularity

The hardest challenge in this assignment was deciding how much detail to include in each diagram. For the Patient object state diagram, an early draft had over 15 states — including separate states for each vital sign being monitored. This was technically accurate but completely unreadable. Collapsing vital-specific states into a single **UnderObservation** state made the diagram cleaner and more useful without losing meaningful information.

The same tension appeared in activity diagrams. The Vital Ingestion workflow could have included separate branches for each of the five vital signs. Instead, the diagram treats all vitals uniformly and focuses on the threshold evaluation logic — which is where the real complexity lies. The lesson: diagrams should communicate intent, not exhaustively document every edge case.

### Aligning Diagrams with Agile User Stories

Mapping state diagrams to user stories revealed gaps in the original sprint planning. For example, the Report object state diagram includes a **Failed** state and a retry path — but neither the user story (US-010) nor the sprint tasks addressed what happens when report generation times out. This is exactly the kind of detail that UML modeling surfaces that prose requirements miss.

Activity diagrams also made sprint task estimates feel more concrete. Seeing the Ward Dashboard Monitoring workflow mapped across 5 sprint tasks (T-010 to T-016) made it clear why that user story was assigned 5 story points — the workflow is genuinely complex, with WebSocket connections, Redis cache reads, real-time rendering, and alert escalation all happening in a continuous loop.

### State Diagrams vs. Activity Diagrams

These two diagram types serve fundamentally different purposes and complement each other well:

**State Transition Diagrams** answer: *"What can this object be, and what causes it to change?"* They are object-centric and focus on lifecycle. The Alert object state diagram, for example, makes the escalation rule (unacknowledged for 5 minutes → escalated) a first-class concept that is easy to reason about and test.

**Activity Diagrams** answer: *"How does this process flow from start to finish?"* They are process-centric and focus on sequence, decisions, and parallel actions. The Alert Acknowledgement activity diagram shows the nurse's actions, the system's responses, and the audit trail creation as a connected workflow — something a state diagram cannot capture.

Used together, they provide a complete picture: state diagrams define the rules, activity diagrams show how those rules play out in practice.
