# domain_model_reflection.md — Reflection on Domain Modeling and Class Diagram Design
## Hospital Patient Monitoring System (HPMS)

---

## Reflection: Challenges and Insights in Domain and Class Diagram Design

### 1. Challenges in Designing the Domain Model

The first challenge was **deciding what counts as a domain entity versus a value or attribute**. For example, early drafts of the domain model included `VitalSign` as a separate entity with its own class — representing heart rate, blood pressure, SpO2, temperature, and respiratory rate as individual objects. After reflection, this felt over-engineered. A single vital reading always contains all five measurements simultaneously, and splitting them into separate objects would complicate every query and relationship in the system. Collapsing all five into a single `VitalReading` class with five numeric attributes simplified the model significantly without losing any meaningful information.

A similar decision arose with `Notification`. Initially, in-app notifications and email notifications were modeled as separate entities with their own persistence. But notifications are ephemeral — they are delivered and then they are done. Modelling them as a `NotificationService` (a stateless service class) rather than a persistent entity was more accurate and avoided cluttering the domain model with data that does not need to be stored long-term.

The hardest abstraction challenge was the **AlertThreshold entity**. Thresholds exist in two forms: system-wide defaults and per-patient overrides. Modeling these as two separate classes would have created unnecessary inheritance complexity. Modeling them as one class with an `isPatientOverride` boolean flag kept the design simple while capturing the distinction. The trade-off is that querying "all system defaults" requires filtering by the flag — a minor query complexity in exchange for a much simpler class structure.

---

### 2. Alignment with Previous Assignments

The class diagram was deliberately designed to align with every prior assignment:

**Assignment 4 (Requirements):** Every class attribute maps to a functional requirement. The `Alert` class's `severity` attribute maps to FR-05; the `AuditLog` class's immutability (no `delete()` method) maps to FR-12. Business rules in the domain model (BR-01 to BR-10) are direct translations of acceptance criteria from the SRD.

**Assignment 5 (Use Cases):** Class methods correspond directly to use case actions. `Patient.register()` maps to UC-01; `Alert.acknowledge()` maps to UC-06; `Report.generate()` maps to UC-09. Every method has a traceable origin in a use case actor interaction.

**Assignment 8 (State and Activity Diagrams):** The states in the Patient state diagram (Registered, Admitted, UnderObservation, Critical, Discharged) are reflected in the `Patient.status` attribute. The transitions in the Alert state diagram (Generated, Dispatched, Acknowledged, Escalated, Closed) map exactly to the `Alert` class methods: `trigger()`, `acknowledge()`, `escalate()`, `close()`. This alignment confirms that the behavioral models and the structural model are consistent.

**Assignment 3 (Architecture):** The Level 4 Code Diagram in ARCHITECTURE.md was an early sketch of the class structure. The full class diagram in this assignment significantly expands that sketch — adding service classes (AlertEngine, NotificationService), completing method signatures, and formalizing all relationship multiplicities.

---

### 3. Trade-offs Made

**Inheritance vs. Composition for User Roles:**
The most significant trade-off was deciding not to use inheritance for user roles. An object-oriented purist might model `Nurse`, `Doctor`, and `Admin` as subclasses of `User`, each with role-specific methods. This was rejected for two reasons: first, a user's role can change (a nurse can be promoted to admin) — inheritance is poorly suited to mutable classification. Second, role-based behaviour in HPMS is enforced at the API middleware level, not at the object level. A single `User` class with a `role` attribute and role-checked methods is simpler, more flexible, and truer to the actual implementation.

**Service Classes vs. Entity Classes:**
AlertEngine and NotificationService were modeled as service classes rather than entities. This reflects a deliberate architectural choice: these components have no persistent state of their own — they process data and delegate storage to other classes. Modeling them as entities would have implied they needed database tables, which they do not. The `<<service>>` stereotype makes this explicit.

**Simplifying AuditLog:**
The AuditLog class has no `update()` or `delete()` methods — intentionally. This is not an oversight but a business rule (BR-07): audit logs are immutable. Omitting mutation methods from the class definition enforces this rule at the design level, making it harder for future developers to accidentally implement a log deletion feature.

---

### 4. Lessons Learned About Object-Oriented Design

The most important lesson from this assignment is that **good OO design is about finding the right level of abstraction**. Too granular and the model becomes unmanageable; too coarse and it loses the ability to express meaningful business rules.

A second lesson is that **relationships are as important as classes**. The decision to use composition versus aggregation versus association is not cosmetic — it encodes business logic. The composition relationship between Patient and VitalReading communicates that readings cannot exist without a patient, which has direct implications for database cascades, data deletion policies, and API design.

Finally, this assignment demonstrated that **UML class diagrams are a communication tool, not just a documentation artifact**. The process of drawing the diagram forced decisions that prose requirements never surfaced — like whether AlertThreshold should be one class or two, or whether User roles should use inheritance. These are decisions that would otherwise have been made ad hoc during coding, with potentially inconsistent results. Making them explicitly during design produces a more coherent system.
