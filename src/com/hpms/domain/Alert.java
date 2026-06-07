package com.hpms.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Alert implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID alertId;
    private final UUID patientId;
    private final UUID vitalReadingId;
    private final String vitalType;
    private final double triggeredValue;
    private String severity;
    private String status;
    private LocalDateTime triggeredAt;
    private LocalDateTime acknowledgedAt;
    private UUID acknowledgedById;

    public Alert(UUID patientId, UUID vitalReadingId, String vitalType, double triggeredValue, String severity) {
        this.alertId = UUID.randomUUID();
        this.patientId = patientId;
        this.vitalReadingId = vitalReadingId;
        this.vitalType = vitalType;
        this.triggeredValue = triggeredValue;
        this.severity = severity;
        this.status = "NEW";
    }

    public void trigger() {
        this.status = "OPEN";
        this.triggeredAt = LocalDateTime.now();
    }

    public void acknowledge(UUID userId) {
        if (!"OPEN".equals(status) && !"ESCALATED".equals(status)) {
            throw new IllegalStateException("Only OPEN or ESCALATED alerts can be acknowledged.");
        }
        this.status = "ACKNOWLEDGED";
        this.acknowledgedById = userId;
        this.acknowledgedAt = LocalDateTime.now();
    }

    public void escalate() {
        if ("OPEN".equals(status)) {
            this.status = "ESCALATED";
            this.severity = "CRITICAL";
        }
    }

    public void close() {
        this.status = "CLOSED";
    }

    public UUID getAlertId() {
        return alertId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getVitalReadingId() {
        return vitalReadingId;
    }

    public String getVitalType() {
        return vitalType;
    }

    public double getTriggeredValue() {
        return triggeredValue;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public UUID getAcknowledgedById() {
        return acknowledgedById;
    }
}