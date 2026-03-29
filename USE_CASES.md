# Use Case Diagram

```mermaid
graph TD

Doctor --> ViewVitals
Doctor --> ReceiveAlerts
Doctor --> ReviewHistory

Nurse --> RecordVitals
Nurse --> MonitorPatients
Nurse --> TriggerAlert

Admin --> ManageUsers
Admin --> ConfigureSystem

Technician --> MaintainSystem
Technician --> MonitorSensors

Patient --> ViewOwnVitals

Sensor --> SendVitals

ViewVitals --> Authenticate
RecordVitals --> Authenticate
MonitorPatients --> Authenticate