# 🏥 Hospital Patient Monitoring System (HPMS)

A real-time distributed system for continuous monitoring of patient vitals, alert generation, and clinical coordination across hospital wards. The system is designed to improve response time to patient deterioration, reduce manual monitoring overhead, and support data-driven clinical decision-making.

---

## 📌 Project Overview

The Hospital Patient Monitoring System (HPMS) enables healthcare professionals to:

- Continuously monitor patient vital signs in real time
- Receive immediate alerts when readings exceed safe thresholds
- Access patient history and trend data for clinical assessment
- Coordinate care across doctors, nurses, and administrative staff
- Maintain secure, role-based access to sensitive medical data

The system prioritises **patient safety, system reliability, and clinical efficiency** under high-load hospital environments.

---

## 📂 Project Documentation

- 📄 **[SPECIFICATION.md](./SPECIFICATION.md)**  
  System overview including domain context, scope, and problem definition

- 🏗️ **[ARCHITECTURE.md](./ARCHITECTURE.md)**  
  C4 model architecture (Context, Container, Component, Code diagrams)

- 👥 **[STAKEHOLDERS.md](./STAKEHOLDERS.md)**  
  Stakeholder analysis including goals, concerns, and system conflicts

- 📋 **[SRD.md](./SRD.md)**  
  System Requirements Document (12 functional + 9 non-functional requirements)

- 🧪 **[USE_CASE_SPEC.md](./USE_CASE_SPEC.md)**  
  Detailed use case specifications for system interactions

- 💭 **[REFLECTION.md](./REFLECTION.md)**  
  Analysis of stakeholder conflicts and trade-off decisions in requirements engineering

- 🏃 **[AGILE_PLANNING.md](./AGILE_PLANNING.md)**  
  User stories, MoSCoW product backlog, and Sprint 1 plan

- 🔄 **[AGILE_REFLECTION.md](./AGILE_REFLECTION.md)**  
  Reflection on Agile planning challenges as a solo developer

- 📊 **[template_analysis.md](./TEMPLATE_ANALYSIS.md)**  
  Comparison of GitHub project templates and justification for Automated Kanban selection

- 🗂️ **[kanban_explanation.md](./KANBAN_EXPLANATION.md)**  
  Definition and purpose of the HPMS Kanban board and customization choices

- 🔄 **[STATE_DIAGRAMS.md](./STATE_DIAGRAMS.md)**  
  UML state transition diagrams for 8 core system objects

- 🔀 **[ACTIVITY_DIAGRAMS.md](./ACTIVITY_DIAGRAMS.md)**  
  UML activity workflow diagrams for 8 core system workflows

- 💭 **[modeling_reflection.md](./modeling_reflection.md)**  
  Reflection on state and activity diagram modeling challenges

---

## 🚀 Core System Capabilities

Once implemented, HPMS will provide:

- 📡 **Real-time Monitoring** — Live tracking of vital signs using WebSocket communication
- 🚨 **Smart Alerting System** — Multi-level alerts (warning/critical) based on configurable thresholds
- 📊 **Clinical Dashboard** — Role-based dashboards tailored for nurses, doctors, and admins
- 🔐 **Secure Access Control** — JWT-based authentication with role-based permissions
- 🧾 **Patient History Tracking** — Time-series storage of patient vitals for trend analysis
- 🏥 **Hospital Integration Ready** — Designed for interoperability with EHR systems

---

## 🧑‍⚕️ System Roles

- **Doctors** → Monitor patients, review history, adjust thresholds
- **Nurses** → Record vitals, respond to alerts, monitor wards
- **Administrators** → Manage users, system configuration, access control
- **Technicians** → Maintain system infrastructure and sensor reliability
- **Patients (Read-only)** → View personal health data (if enabled)

---

## 🛠️ Technology Stack

| Layer        | Technology |
|--------------|------------|
| Frontend     | React.js   |
| Backend      | Node.js / Express |
| Database     | PostgreSQL |
| Real-time    | WebSockets |
| Authentication | JWT + RBAC |
| Deployment   | Docker / AWS |

---

## 🎯 Design Principles

- **Low latency for clinical safety**
- **High availability under hospital load**
- **Strict role-based security model**
- **Separation of concerns across system layers**
- **Scalable real-time architecture**

---

## 📌 Status

> This project is currently in the design and specification phase. Implementation will follow after full requirements validation and architectural finalisation.
