package com.hpms.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Patient implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID patientId;
    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private String diagnosis;
    private String status;
    private LocalDateTime admittedAt;
    private LocalDateTime dischargedAt;
    private UUID wardId;
    private final List<VitalReading> vitalReadings;
    private final List<Alert> alerts;
    private final List<AlertThreshold> alertThresholdOverrides;
    private final List<Report> reports;

    public Patient(String firstName, String lastName, LocalDate dateOfBirth, String diagnosis) {
        this.patientId = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.diagnosis = diagnosis;
        this.status = "REGISTERED";
        this.vitalReadings = new ArrayList<>();
        this.alerts = new ArrayList<>();
        this.alertThresholdOverrides = new ArrayList<>();
        this.reports = new ArrayList<>();
    }

    public void register() {
        this.status = "ADMITTED";
        this.admittedAt = LocalDateTime.now();
        this.dischargedAt = null;
    }

    public void discharge() {
        this.status = "DISCHARGED";
        this.dischargedAt = LocalDateTime.now();
        this.wardId = null;
        this.alertThresholdOverrides.clear();
    }

    public void assignToWard(UUID wardId) {
        this.wardId = wardId;
    }

    public List<VitalReading> getVitalHistory(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return vitalReadings.stream()
                .filter(v -> v.getRecordedAt().isAfter(cutoff))
                .toList();
    }

    public List<Alert> getActiveAlerts() {
        return alerts.stream()
                .filter(a -> "OPEN".equals(a.getStatus()) || "ESCALATED".equals(a.getStatus()))
                .toList();
    }

    public void addVitalReading(VitalReading reading) {
        if (!patientId.equals(reading.getPatientId())) {
            throw new IllegalArgumentException("Reading patientId does not match this patient.");
        }
        vitalReadings.add(reading);
    }

    public void addAlert(Alert alert) {
        if (!patientId.equals(alert.getPatientId())) {
            throw new IllegalArgumentException("Alert patientId does not match this patient.");
        }
        alerts.add(alert);
    }

    public void addAlertThresholdOverride(AlertThreshold threshold) {
        threshold.applyToPatient(patientId);
        alertThresholdOverrides.add(threshold);
    }

    public void addReport(Report report) {
        if (!patientId.equals(report.getPatientId())) {
            throw new IllegalArgumentException("Report patientId does not match this patient.");
        }
        reports.add(report);
    }

    public UUID getPatientId() {
        return patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getAdmittedAt() {
        return admittedAt;
    }

    public LocalDateTime getDischargedAt() {
        return dischargedAt;
    }

    public UUID getWardId() {
        return wardId;
    }

    public List<VitalReading> getVitalReadings() {
        return Collections.unmodifiableList(vitalReadings);
    }

    public List<Alert> getAlerts() {
        return Collections.unmodifiableList(alerts);
    }

    public List<AlertThreshold> getAlertThresholdOverrides() {
        return Collections.unmodifiableList(alertThresholdOverrides);
    }

    public List<Report> getReports() {
        return Collections.unmodifiableList(reports);
    }
}