# ARCHITECTURE.md — Hospital Patient Monitoring System

---

## Project Title
**Hospital Patient Monitoring System (HPMS)**

## Domain
**Healthcare / Hospital** — Real-time patient vital signs monitoring and alert management across a hospital ward.

## Problem Statement
Hospitals need a centralized system that ingests patient vitals from bedside sensors, triggers alerts on threshold breaches, and gives clinical staff instant access to patient data through a unified dashboard.

## Individual Scope
Scoped to a single ward, three user roles (Doctor, Nurse, Admin), simulated IoT sensor data, and a web-based interface. Full feasibility justification in [SPECIFICATION.md](./SPECIFICATION.md).

---

## C4 Diagrams

The C4 model describes the architecture at four levels of zoom:
1. **Level 1 — System Context**: Who uses the system and what external systems does it touch?
2. **Level 2 — Container**: What are the major deployable parts of the system?
3. **Level 3 — Component**: What are the key components inside each container?
4. **Level 4 — Code**: Class/entity level detail for a key component

---

## Level 1 — System Context Diagram

> Shows HPMS in relation to its users and external systems.

```mermaid
C4Context
    title System Context — Hospital Patient Monitoring System

    Person(nurse, "Nurse", "Monitors patient vitals, acknowledges alerts, logs observations")
    Person(doctor, "Doctor", "Reviews patient vitals, updates treatment notes, receives critical alerts")
    Person(admin, "Admin", "Manages users, wards, and system configuration")

    System(hpms, "Hospital Patient Monitoring System", "Real-time monitoring of patient vitals with alerting, dashboards, and reporting")

    System_Ext(iot, "IoT Sensor Network", "Bedside devices that measure and transmit patient vitals (simulated in v1)")
    System_Ext(ehr, "EHR System", "External Electronic Health Records system; receives discharge summaries")
    System_Ext(email, "Email / SMS Gateway", "Sends critical alert notifications to clinical staff")

    Rel(nurse, hpms, "Views vitals, acknowledges alerts", "HTTPS")
    Rel(doctor, hpms, "Reviews patients, updates treatment plans", "HTTPS")
    Rel(admin, hpms, "Configures system and manages users", "HTTPS")
    Rel(iot, hpms, "Pushes vital readings", "WebSocket / HTTP")
    Rel(hpms, ehr, "Sends discharge summaries", "HL7 FHIR / REST")
    Rel(hpms, email, "Sends critical alert notifications", "SMTP / REST API")
```

---

## Level 2 — Container Diagram

> Shows the major deployable components (containers) of HPMS.

```mermaid
C4Container
    title Container Diagram — Hospital Patient Monitoring System

    Person(nurse, "Nurse")
    Person(doctor, "Doctor")
    Person(admin, "Admin")

    System_Ext(iot, "IoT Sensor Network")
    System_Ext(ehr, "EHR System")
    System_Ext(email, "Email / SMS Gateway")

    System_Boundary(hpms, "Hospital Patient Monitoring System") {
        Container(webapp, "Web Application", "React.js", "Browser-based dashboard for nurses, doctors, and admins")
        Container(api, "API Server", "Node.js / Express", "REST API handling all business logic: patients, vitals, alerts, users")
        Container(ws, "WebSocket Server", "Node.js / ws", "Pushes real-time vital updates and alerts to connected clients")
        Container(alertengine, "Alert Engine", "Node.js Service", "Evaluates incoming vitals against thresholds; triggers alerts")
        Container(db, "Primary Database", "PostgreSQL", "Stores patients, vitals history, alerts, users, and ward data")
        Container(cache, "Cache", "Redis", "Stores latest vitals per patient for fast dashboard reads")
        Container(simulator, "Sensor Simulator", "Node.js Script", "Simulates IoT sensor data in development/demo environment")
    }

    Rel(nurse, webapp, "Uses", "HTTPS")
    Rel(doctor, webapp, "Uses", "HTTPS")
    Rel(admin, webapp, "Uses", "HTTPS")

    Rel(webapp, api, "API calls", "REST / HTTPS")
    Rel(webapp, ws, "Subscribes to live updates", "WebSocket")

    Rel(api, db, "Reads/writes data", "SQL")
    Rel(api, cache, "Reads latest vitals", "Redis protocol")
    Rel(api, alertengine, "Forwards incoming vitals", "Internal")

    Rel(alertengine, db, "Writes alerts", "SQL")
    Rel(alertengine, ws, "Pushes alert events", "Internal")
    Rel(alertengine, email, "Sends critical notifications", "REST API")

    Rel(ws, cache, "Reads latest vitals", "Redis protocol")

    Rel(iot, api, "Pushes vital readings", "HTTP POST")
    Rel(simulator, api, "Simulates sensor data", "HTTP POST")
    Rel(api, ehr, "Sends discharge summaries", "REST / FHIR")
```

---

## Level 3 — Component Diagram (API Server)

> Zooms into the API Server container to show its internal components.

```mermaid
C4Component
    title Component Diagram — API Server (Node.js / Express)

    Container_Ext(webapp, "Web Application", "React.js")
    Container_Ext(db, "PostgreSQL Database")
    Container_Ext(cache, "Redis Cache")
    Container_Ext(alertengine, "Alert Engine")
    Container_Ext(iot, "IoT Sensors / Simulator")

    Container_Boundary(api, "API Server") {
        Component(authcontroller, "Auth Controller", "Express Router", "Handles login, JWT token issuance and validation, role-based access control")
        Component(patientcontroller, "Patient Controller", "Express Router", "CRUD for patient records: registration, assignment, discharge")
        Component(vitalscontroller, "Vitals Controller", "Express Router", "Receives incoming vital readings; writes to DB and cache; forwards to Alert Engine")
        Component(alertcontroller, "Alert Controller", "Express Router", "Retrieves alerts; handles acknowledgement; manages threshold configuration")
        Component(usercontroller, "User Controller", "Express Router", "Admin-only: manage doctors, nurses, ward assignments")
        Component(reportcontroller, "Report Controller", "Express Router", "Generates PDF reports for patient vitals history and ward summaries")
        Component(authmiddleware, "Auth Middleware", "Express Middleware", "Validates JWT tokens; enforces role-based route access")
        Component(patientservice, "Patient Service", "Business Logic", "Core patient logic: validation, ward capacity checks, discharge workflow")
        Component(vitalsservice, "Vitals Service", "Business Logic", "Processes and stores vital readings; updates Redis cache with latest values")
        Component(alertservice, "Alert Service", "Business Logic", "Evaluates vitals against thresholds; creates alert records; triggers notifications")
    }

    Rel(webapp, authcontroller, "POST /auth/login", "REST")
    Rel(webapp, patientcontroller, "GET/POST /patients", "REST")
    Rel(webapp, vitalscontroller, "GET /vitals/:patientId", "REST")
    Rel(webapp, alertcontroller, "GET/PUT /alerts", "REST")
    Rel(webapp, reportcontroller, "GET /reports/:patientId", "REST")

    Rel(iot, vitalscontroller, "POST /vitals/ingest", "REST")

    Rel(authcontroller, authmiddleware, "Issues token")
    Rel(patientcontroller, authmiddleware, "Protected by")
    Rel(vitalscontroller, authmiddleware, "Protected by")

    Rel(patientcontroller, patientservice, "Delegates to")
    Rel(vitalscontroller, vitalsservice, "Delegates to")
    Rel(alertcontroller, alertservice, "Delegates to")

    Rel(patientservice, db, "SQL queries")
    Rel(vitalsservice, db, "SQL insert/select")
    Rel(vitalsservice, cache, "SET latest vitals")
    Rel(vitalsservice, alertengine, "Forward readings")
    Rel(alertservice, db, "Write alert records")
```

---

## Level 4 — Code Diagram (Vitals Service — Key Entities)

> Shows the data model and key classes for the Vitals Service component.

```mermaid
classDiagram
    class Patient {
        +UUID id
        +String firstName
        +String lastName
        +Date dateOfBirth
        +String diagnosis
        +UUID wardId
        +UUID bedId
        +UUID assignedDoctorId
        +String status
        +Date admittedAt
        +Date dischargedAt
        +register()
        +discharge()
        +assignToWard()
    }

    class VitalReading {
        +UUID id
        +UUID patientId
        +Float heartRate
        +Float bloodPressureSystolic
        +Float bloodPressureDiastolic
        +Float oxygenSaturation
        +Float temperature
        +Float respiratoryRate
        +DateTime recordedAt
        +String sourceDeviceId
        +save()
        +getHistory()
    }

    class AlertThreshold {
        +UUID id
        +UUID patientId
        +String vitalType
        +Float minValue
        +Float maxValue
        +String severity
        +Boolean isPatientOverride
        +validate()
        +getDefaults()
    }

    class Alert {
        +UUID id
        +UUID patientId
        +UUID vitalReadingId
        +String vitalType
        +Float triggeredValue
        +String severity
        +String status
        +DateTime triggeredAt
        +DateTime acknowledgedAt
        +UUID acknowledgedById
        +trigger()
        +acknowledge()
        +escalate()
    }

    class User {
        +UUID id
        +String firstName
        +String lastName
        +String email
        +String role
        +UUID wardId
        +Boolean isActive
        +login()
        +logout()
        +resetPassword()
    }

    class Ward {
        +UUID id
        +String name
        +String floor
        +Int capacity
        +Int currentOccupancy
        +getPatients()
        +getActiveAlerts()
    }

    Patient "1" --> "many" VitalReading : has
    Patient "1" --> "many" Alert : generates
    Patient "1" --> "many" AlertThreshold : has overrides
    VitalReading "1" --> "0..1" Alert : triggers
    User "1" --> "many" Alert : acknowledges
    Ward "1" --> "many" Patient : contains
    User "many" --> "1" Ward : assigned to
```

---

## End-to-End Data Flow

```mermaid
sequenceDiagram
    participant Sensor as IoT Sensor
    participant API as API Server
    participant Cache as Redis Cache
    participant DB as PostgreSQL
    participant AE as Alert Engine
    participant WS as WebSocket Server
    participant UI as Nurse Dashboard

    Sensor->>API: POST /vitals/ingest {patientId, heartRate, SpO2, ...}
    API->>DB: INSERT vital reading
    API->>Cache: SET latest vitals for patient
    API->>AE: Forward reading for threshold check
    AE->>DB: Fetch alert thresholds for patient
    AE-->>AE: Evaluate vitals vs thresholds
    alt Threshold breached
        AE->>DB: INSERT alert record
        AE->>WS: Emit alert event {patientId, type, severity}
        WS->>UI: Push alert notification
        UI-->>UI: Display alert banner
    else All values normal
        AE-->>API: No alert triggered
    end
    WS->>UI: Push updated vitals
    UI-->>UI: Refresh patient vital display
```
