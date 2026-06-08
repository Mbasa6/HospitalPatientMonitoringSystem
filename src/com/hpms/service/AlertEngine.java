package com.hpms.service;

import com.hpms.domain.Alert;
import com.hpms.domain.AlertThreshold;
import com.hpms.domain.VitalReading;

import java.util.List;
import java.util.UUID;

public class AlertEngine {
    private final NotificationService notificationService;

    public AlertEngine(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public Alert evaluateReading(VitalReading reading, List<AlertThreshold> thresholds) {
        for (AlertThreshold threshold : thresholds) {
            if (isBreached(reading, threshold)) {
                Alert alert = generateAlert(reading, threshold);
                alert.trigger();
                dispatchNotification(alert);
                return alert;
            }
        }
        return null;
    }

    public List<AlertThreshold> getThresholds(UUID patientId) {
        return AlertThreshold.getDefaults().stream()
                .filter(threshold -> threshold.getPatientId() == null || patientId.equals(threshold.getPatientId()))
                .toList();
    }

    public Alert generateAlert(VitalReading reading, AlertThreshold threshold) {
        double value = getValueByType(reading, threshold.getVitalType());
        return new Alert(
                reading.getPatientId(),
                reading.getReadingId(),
                threshold.getVitalType(),
                value,
                threshold.getSeverity()
        );
    }

    public void dispatchNotification(Alert alert) {
        UUID defaultOnCall = UUID.fromString("00000000-0000-0000-0000-000000000002");
        notificationService.sendInAppNotification(defaultOnCall, alert);
        
        if ("CRITICAL".equals(alert.getSeverity())) {
            notificationService.sendEmailNotification("emergency@hospital.com", alert);
        }
    }

    private boolean isBreached(VitalReading reading, AlertThreshold threshold) {
        double value = getValueByType(reading, threshold.getVitalType());
        return value < threshold.getMinValue() || value > threshold.getMaxValue();
    }

    private double getValueByType(VitalReading reading, String vitalType) {
        return switch (vitalType) {
            case "heartRate" -> reading.getHeartRate();
            case "bloodPressureSystolic" -> reading.getBloodPressureSystolic();
            case "bloodPressureDiastolic" -> reading.getBloodPressureDiastolic();
            case "oxygenSaturation" -> reading.getOxygenSaturation();
            case "temperature" -> reading.getTemperature();
            case "respiratoryRate" -> reading.getRespiratoryRate();
            default -> throw new IllegalArgumentException("Unsupported vitalType: " + vitalType);
        };
    }
}