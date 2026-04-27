# CLASS_DIAGRAM.md — Class Diagram
## Hospital Patient Monitoring System (HPMS)

> Traces to: [DOMAIN_MODEL.md](./DOMAIN_MODEL.md) | [SRD.md](./SRD.md) | [STATE_DIAGRAMS.md](./STATE_DIAGRAMS.md)

---

## Class Diagram

```mermaid
classDiagram
    class Patient {
        -patientId: UUID
        -firstName: String
        -lastName: String
        -dateOfBirth: Date
        -diagnosis: String
        -status: String
        -admittedAt: DateTime
        -dischargedAt: DateTime
        +register() void
        +discharge() void
        +assignToWard(wardId: UUID) void
        +getVitalHistory(days: int) List~VitalReading~
        +getActiveAlerts() List~Alert~
    }

    class VitalReading {
        -readingId: UUID
        -patientId: UUID
        -heartRate: Float
        -bloodPressureSystolic: Float
        -bloodPressureDiastolic: Float
        -oxygenSaturation: Float
        -temperature: Float
        -respiratoryRate: Float
        -recordedAt: DateTime
        -sourceDeviceId: String
        +save() void
        +validate() Boolean
        +getLatest(patientId: UUID) VitalReading
    }

    class Alert {
        -alertId: UUID
        -patientId: UUID
        -vitalReadingId: UUID
        -vitalType: String
        -triggeredValue: Float
        -severity: String
        -status: String
        -triggeredAt: DateTime
        -acknowledgedAt: DateTime
        -acknowledgedById: UUID
        +trigger() void
        +acknowledge(userId: UUID) void
        +escalate() void
        +close() void
    }

    class AlertThreshold {
        -thresholdId: UUID
        -patientId: UUID
        -vitalType: String
        -minValue: Float
        -maxValue: Float
        -severity: String
        -isPatientOverride: Boolean
        -createdById: UUID
        +validate() Boolean
        +applyToPatient(patientId: UUID) void
        +revertToDefault() void
        +getDefaults() List~AlertThreshold~
    }

    class User {
        -userId: UUID
        -firstName: String
        -lastName: String
        -email: String
        -passwordHash: String
        -role: String
        -wardId: UUID
        -isActive: Boolean
        -lastLoginAt: DateTime
        +login(email: String, password: String) String
        +logout() void
        +resetPassword(newPassword: String) void
        +deactivate() void
    }

    class Ward {
        -wardId: UUID
        -name: String
        -floor: String
        -capacity: int
        -currentOccupancy: int
        -status: String
        +addPatient(patient: Patient) void
        +removePatient(patientId: UUID) void
        +getActiveAlerts() List~Alert~
        +getSummary() WardSummary
        +isAtCapacity() Boolean
    }

    class Report {
        -reportId: UUID
        -patientId: UUID
        -generatedById: UUID
        -dateRangeStart: Date
        -dateRangeEnd: Date
        -generatedAt: DateTime
        -filePath: String
        -status: String
        +generate() void
        +download() File
        +archive() void
    }

    class AuditLog {
        -logId: UUID
        -userId: UUID
        -action: String
        -targetEntity: String
        -targetId: UUID
        -timestamp: DateTime
        -ipAddress: String
        +create(action: String, targetEntity: String, targetId: UUID) void
        +export(format: String) File
    }

    class AlertEngine {
        <<service>>
        +evaluateReading(reading: VitalReading) void
        +getThresholds(patientId: UUID) List~AlertThreshold~
        +generateAlert(reading: VitalReading, threshold: AlertThreshold) Alert
        +dispatchNotification(alert: Alert) void
    }

    class NotificationService {
        <<service>>
        +sendInAppNotification(userId: UUID, alert: Alert) void
        +sendEmailNotification(email: String, alert: Alert) void
        +logDelivery(alertId: UUID, channel: String) void
    }

    %% Relationships
    Patient "1" *-- "0..*" VitalReading : has
    Patient "1" *-- "0..*" Alert : generates
    Patient "1" o-- "0..*" AlertThreshold : has overrides
    Patient "0..*" --o "1" Ward : assigned to
    VitalReading "1" --> "0..1" Alert : triggers
    Alert "0..*" --> "1" User : acknowledged by
    User "0..*" --o "1" Ward : assigned to
    User "1" --> "0..*" Report : generates
    Patient "1" *-- "0..*" Report : has
    User "1" --> "0..*" AuditLog : creates
    AlertEngine ..> VitalReading : evaluates
    AlertEngine ..> AlertThreshold : fetches
    AlertEngine ..> Alert : creates
    AlertEngine ..> NotificationService : uses
    NotificationService ..> User : notifies
```

---

## Key Design Decisions

### 1. Composition vs. Aggregation
**Patient → VitalReading** and **Patient → Alert** use **composition** (filled diamond) because vital readings and alerts are meaningless without the patient they belong to — if a patient record is deleted, all associated readings and alerts are deleted too.

**Patient → AlertThreshold** uses **aggregation** (open diamond) because thresholds can exist as system-wide defaults independent of any specific patient. A threshold configuration is not owned exclusively by one patient.

**Ward → Patient** uses **aggregation** because a patient can exist before being assigned to a ward and can be reassigned — the ward does not own the patient.

### 2. Service Classes
**AlertEngine** and **NotificationService** are modeled as service classes (marked with `<<service>>`) rather than entities. They have no persistent state — they perform operations on domain objects. This reflects the separation of concerns in the system architecture (ARCHITECTURE.md) where the Alert Engine is a distinct container.

### 3. Dependency (Dashed Arrow)
AlertEngine and NotificationService use **dependency** relationships (dashed arrows) to VitalReading, AlertThreshold, Alert, and User — they use these objects temporarily during method execution but do not own or store them.

### 4. Multiplicity
- `Patient "1" *-- "0..*" VitalReading` — one patient has zero or more vital readings
- `VitalReading "1" --> "0..1" Alert` — one reading triggers at most one alert
- `Alert "0..*" --> "1" User` — many alerts can be acknowledged by one user
- `Patient "0..*" --o "1" Ward` — many patients assigned to one ward

### 5. Alignment with Prior Assignments
- All 8 entities map directly to the domain model (DOMAIN_MODEL.md)
- Attributes match the database schema defined in ARCHITECTURE.md (Level 4 Code Diagram)
- Methods correspond to actions defined in activity diagrams (ACTIVITY_DIAGRAMS.md)
- Relationships enforce the business rules defined in DOMAIN_MODEL.md (BR-01 to BR-10)
