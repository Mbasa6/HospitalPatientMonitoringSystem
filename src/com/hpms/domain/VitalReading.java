package com.hpms.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class VitalReading implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID readingId;
    private final UUID patientId;
    private final double heartRate;
    private final double bloodPressureSystolic;
    private final double bloodPressureDiastolic;
    private final double oxygenSaturation;
    private final double temperature;
    private final double respiratoryRate;
    private LocalDateTime recordedAt;
    private final String sourceDeviceId;

    public VitalReading(UUID patientId,
                        double heartRate,
                        double bloodPressureSystolic,
                        double bloodPressureDiastolic,
                        double oxygenSaturation,
                        double temperature,
                        double respiratoryRate,
                        String sourceDeviceId) {
        this.readingId = UUID.randomUUID();
        this.patientId = patientId;
        this.heartRate = heartRate;
        this.bloodPressureSystolic = bloodPressureSystolic;
        this.bloodPressureDiastolic = bloodPressureDiastolic;
        this.oxygenSaturation = oxygenSaturation;
        this.temperature = temperature;
        this.respiratoryRate = respiratoryRate;
        this.recordedAt = LocalDateTime.now();
        this.sourceDeviceId = sourceDeviceId;
    }

    public void save() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }

    public boolean validate() {
        return patientId != null
                && heartRate > 0
                && bloodPressureSystolic > 0
                && bloodPressureDiastolic > 0
                && oxygenSaturation > 0
                && oxygenSaturation <= 100
                && temperature > 0
                && respiratoryRate > 0;
    }

    public static VitalReading getLatest(List<VitalReading> readings, UUID patientId) {
        return readings.stream()
                .filter(r -> r.getPatientId().equals(patientId))
                .max(Comparator.comparing(VitalReading::getRecordedAt))
                .orElse(null);
    }

    public UUID getReadingId() {
        return readingId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public double getBloodPressureSystolic() {
        return bloodPressureSystolic;
    }

    public double getBloodPressureDiastolic() {
        return bloodPressureDiastolic;
    }

    public double getOxygenSaturation() {
        return oxygenSaturation;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getRespiratoryRate() {
        return respiratoryRate;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public String getSourceDeviceId() {
        return sourceDeviceId;
    }
}