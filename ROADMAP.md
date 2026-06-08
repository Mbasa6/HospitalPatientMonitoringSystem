# HPMS Roadmap

This document outlines planned features and improvements for the Hospital Patient Monitoring System beyond the current assignment scope.

---

## Near-Term (Sprint 2)

| Feature | Description | Issue |
|---|---|---|
| Live vitals dashboard | Real-time WebSocket-powered dashboard for nurses | #2 |
| Email notifications | Send email alerts for critical patient vitals | #7 |
| Ward summary dashboard | Aggregated view of all patients per ward | #8 |

---

## Medium-Term

| Feature | Description |
|---|---|
| PDF report generation | Export patient history and vitals as downloadable PDF reports |
| AES-256 data encryption | Encrypt sensitive patient data at rest and in transit |
| Redis caching layer | Cache latest vital readings to reduce database load |
| JWT authentication | Secure API endpoints with role-based JWT tokens |
| Patient admission workflow | Full admit/discharge workflow with ward assignment |

---

## Long-Term

| Feature | Description |
|---|---|
| EHR integration | Interoperability with external Electronic Health Record systems |
| Mobile app support | React Native companion app for bedside nurses |
| Predictive alerts | ML-based early warning scores from vital trend data |
| Multi-hospital support | Tenant isolation for multi-site hospital deployments |
| Audit logging | Immutable audit trail for all security-sensitive actions |

---

## How to Contribute

If you would like to work on any of these features, check the [open issues](https://github.com/Mbasa6/HospitalPatientMonitoringSystem/issues) and look for the `good first issue` or `feature-request` labels. See [CONTRIBUTING.md](CONTRIBUTING.md) for setup instructions.
