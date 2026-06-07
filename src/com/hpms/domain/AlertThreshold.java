package com.hpms.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlertThreshold implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID thresholdId;
    private UUID patientId;
    private final String vitalType;
    private final double minValue;
    private final double maxValue;
    private final String severity;
    private boolean isPatientOverride;
    private final UUID createdById;

    public AlertThreshold(UUID patientId,
                          String vitalType,
                          double minValue,
                          double maxValue,
                          String severity,
                          boolean isPatientOverride,
                          UUID createdById) {
        this.thresholdId = UUID.randomUUID();
        this.patientId = patientId;
        this.vitalType = vitalType;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.severity = severity;
        this.isPatientOverride = isPatientOverride;
        this.createdById = createdById;
    }

    public boolean validate() {
        return minValue < maxValue;
    }

    public void applyToPatient(UUID patientId) {
        this.patientId = patientId;
        this.isPatientOverride = true;
    }

    public void revertToDefault() {
        this.patientId = null;
        this.isPatientOverride = false;
    }

    public static List<AlertThreshold> getDefaults() {
        List<AlertThreshold> defaults = new ArrayList<>();
        UUID systemUser = UUID.fromString("00000000-0000-0000-0000-000000000001");
        defaults.add(new AlertThreshold(null, "heartRate", 60, 100, "WARNING", false, systemUser));
        defaults.add(new AlertThreshold(null, "oxygenSaturation", 92, 100, "CRITICAL", false, systemUser));
        defaults.add(new AlertThreshold(null, "temperature", 36.1, 37.8, "WARNING", false, systemUser));
        defaults.add(new AlertThreshold(null, "respiratoryRate", 12, 20, "WARNING", false, systemUser));
        return defaults;
    }

    public UUID getThresholdId() {
        return thresholdId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public String getVitalType() {
        return vitalType;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public String getSeverity() {
        return severity;
    }

    public boolean isPatientOverride() {
        return isPatientOverride;
    }

    public UUID getCreatedById() {
        return createdById;
    }
}