# SPECIFICATION.md — Hospital Patient Monitoring System

---

## 1. Project Title

**Hospital Patient Monitoring System (HPMS)**

---

## 2. Domain

**Healthcare / Hospital**

Hospitals are complex, high-stakes environments where real-time information flow is critical to patient survival. The healthcare domain encompasses patient care, clinical workflows, medical records, staff coordination, and regulatory compliance. Within this domain, patient monitoring is one of the most time-sensitive processes — delayed responses to deteriorating vitals can result in patient harm or death.

---

## 3. Problem Statement

Hospitals currently rely on a combination of manual checks, disconnected bedside monitors, and paper-based or siloed digital records to track patient vitals. This leads to:

- **Delayed alerts** — nurses may not notice dangerous vital changes immediately
- **Communication gaps** — doctors are not instantly notified of critical readings
- **Data fragmentation** — patient history is spread across multiple systems
- **Human error** — manual recording increases risk of mistakes

The **Hospital Patient Monitoring System (HPMS)** solves this by providing a centralized, real-time platform that:

- Continuously reads vitals from bedside IoT sensors
- Automatically triggers alerts when thresholds are breached
- Maintains a unified patient record accessible to authorized staff
- Enables real-time communication between nurses and doctors
- Produces reports for clinical review and compliance

---

## 4. Individual Scope (Feasibility Justification)

This project is scoped for individual development over a single semester. The following design decisions ensure feasibility:

| Decision | Justification |
|---|---|
| Focus on one hospital ward | Limits data volume and user roles to a manageable scope |
| Simulated IoT sensor data | Removes hardware dependency; data can be mocked via a script |
| Web-based UI only | No mobile app required; browser access covers all staff devices |
| Three user roles only | Doctor, Nurse, Admin — covers core workflows without over-engineering |
| PostgreSQL single database | No microservices complexity; one DB handles all entities |
| No billing/insurance module | Out of scope; keeps focus on monitoring core |

The core deliverable is a **functional web application** that demonstrates end-to-end patient monitoring: from sensor data ingestion → storage → alert generation → dashboard display → report export.

---

## 5. Functional Requirements

### 5.1 Patient Management
- Register new patients and assign them to beds/wards
- View patient profile: name, age, diagnosis, assigned doctor
- Discharge patients and archive their records

### 5.2 Vital Signs Monitoring
- Ingest real-time vital data: heart rate, SpO2, blood pressure, temperature, respiratory rate
- Display live vitals on a per-patient dashboard
- Store historical readings with timestamps

### 5.3 Alert System
- Define configurable thresholds per vital sign (system defaults + per-patient overrides)
- Trigger alerts when readings breach thresholds
- Notify assigned nurse and doctor via in-app notification
- Log all alerts with acknowledgement tracking

### 5.4 User Management
- Role-based access: Doctor, Nurse, Admin
- Doctors: view patients, view vitals, update treatment notes
- Nurses: monitor vitals, acknowledge alerts, update observations
- Admin: manage users, wards, and system configuration

### 5.5 Reporting
- Generate per-patient vital history reports (PDF export)
- Ward-level summary: number of active patients, active alerts
- Alert response time analytics

---

## 6. Non-Functional Requirements

| Category | Requirement |
|---|---|
| Performance | Vital updates must reflect on dashboard within 2 seconds |
| Availability | System uptime target: 99.5% |
| Security | All data encrypted in transit (HTTPS/TLS); JWT authentication |
| Scalability | Must support up to 100 concurrent patients in initial version |
| Usability | Dashboard usable without training by clinical staff |
| Compliance | Data handling aligned with basic HIPAA-style privacy principles |

---

## 7. System Actors

| Actor | Description |
|---|---|
| Patient | Source of vital data (passive; data collected via sensors) |
| Nurse | Primary monitor; acknowledges alerts; records observations |
| Doctor | Reviews vitals; updates treatment plans; receives critical alerts |
| Admin | Manages users, wards, system settings |
| IoT Sensor | Bedside device (simulated) that pushes vital readings to the system |
| EHR System | External system; receives discharge summaries (future integration) |

---

## 8. Constraints

- Single hospital, single ward in v1 (multi-ward as future extension)
- IoT sensor data will be simulated in development/demo
- No real-time video/audio (out of scope)
- No mobile native app in v1
