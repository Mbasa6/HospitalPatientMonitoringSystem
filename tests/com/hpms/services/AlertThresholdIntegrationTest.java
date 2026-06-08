package com.hpms.services;

import com.hpms.domain.Alert;
import com.hpms.domain.AlertThreshold;
import com.hpms.domain.VitalReading;
import com.hpms.repositories.inmemory.InMemoryAlertRepository;
import com.hpms.service.AlertEngine;
import com.hpms.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test covering the threshold-driven alert flow: a {@link VitalReading}
 * is evaluated against {@link AlertThreshold}s by the {@link AlertEngine}, and any
 * breach is persisted and managed through the {@link AlertService}.
 */
class AlertThresholdIntegrationTest {
    private AlertEngine engine;
    private AlertService service;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        engine = new AlertEngine(new NotificationService());
        service = new AlertService(new InMemoryAlertRepository());
        patientId = UUID.randomUUID();
    }

    @Test
    void readingAboveMaxThresholdProducesPersistedAlert() {
        // heartRate default threshold is 60-100; 140 breaches the maximum.
        VitalReading reading = reading(140.0, 98.0, 37.0, 16.0);
        List<AlertThreshold> thresholds = engine.getThresholds(patientId);

        Alert detected = engine.evaluateReading(reading, thresholds);
        assertNotNull(detected, "Engine should detect a breach for an out-of-range reading");
        assertEquals("heartRate", detected.getVitalType());
        assertEquals("OPEN", detected.getStatus());

        Alert persisted = service.createAlert(
                detected.getPatientId(),
                detected.getVitalReadingId(),
                detected.getVitalType(),
                detected.getTriggeredValue(),
                detected.getSeverity());

        Alert fetched = service.getAlertById(persisted.getAlertId());
        assertEquals("heartRate", fetched.getVitalType());
        assertEquals(140.0, fetched.getTriggeredValue());
        assertEquals("WARNING", fetched.getSeverity());
        assertEquals(reading.getReadingId(), fetched.getVitalReadingId());
        assertEquals(1, service.getAllAlerts().size());
    }

    @Test
    void readingBelowMinThresholdProducesPersistedCriticalAlert() {
        // oxygenSaturation default threshold is 92-100; 85 breaches the minimum.
        VitalReading reading = reading(80.0, 85.0, 37.0, 16.0);
        List<AlertThreshold> thresholds = engine.getThresholds(patientId);

        Alert detected = engine.evaluateReading(reading, thresholds);
        assertNotNull(detected);

        Alert persisted = service.createAlert(
                detected.getPatientId(),
                detected.getVitalReadingId(),
                detected.getVitalType(),
                detected.getTriggeredValue(),
                detected.getSeverity());

        Alert fetched = service.getAlertById(persisted.getAlertId());
        assertEquals("oxygenSaturation", fetched.getVitalType());
        assertEquals("CRITICAL", fetched.getSeverity());
        assertEquals(85.0, fetched.getTriggeredValue());
    }

    @Test
    void readingWithinAllThresholdsPersistsNoAlert() {
        // All values sit inside the default ranges, so no breach should be raised.
        VitalReading reading = reading(72.0, 98.0, 37.0, 16.0);
        List<AlertThreshold> thresholds = engine.getThresholds(patientId);

        Alert detected = engine.evaluateReading(reading, thresholds);

        assertNull(detected, "No alert should be detected for an in-range reading");
        assertTrue(service.getAllAlerts().isEmpty(), "Nothing should be persisted when no threshold is breached");
    }

    private VitalReading reading(double heartRate,
                                 double oxygenSaturation,
                                 double temperature,
                                 double respiratoryRate) {
        return new VitalReading(
                patientId,
                heartRate,
                120.0,   // bloodPressureSystolic (no default threshold)
                80.0,    // bloodPressureDiastolic (no default threshold)
                oxygenSaturation,
                temperature,
                respiratoryRate,
                "device-1");
    }
}
