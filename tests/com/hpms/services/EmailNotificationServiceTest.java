package com.hpms.services;

import com.hpms.domain.Alert;
import com.hpms.repositories.inmemory.InMemoryAlertRepository;
import com.hpms.services.exceptions.BusinessRuleException;
import com.hpms.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AlertServiceThresholdTest — Integration Tests for AlertService
 * Relates to: Issue #33 — Write integration test for AlertService threshold
 */
class AlertServiceThresholdTest {

    private AlertService service;

    @BeforeEach
    void setUp() {
        service = new AlertService(new InMemoryAlertRepository());
    }

    // ── 1. Threshold / triggeredValue validation ──────────────────────────

    @Test
    void createAlert_throwsWhenTriggeredValueIsZero() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
            service.createAlert(UUID.randomUUID(), UUID.randomUUID(), "heartRate", 0, "WARNING")
        );
        assertTrue(ex.getMessage().contains("triggeredValue must be greater than 0"));
    }

    @Test
    void createAlert_throwsWhenTriggeredValueIsNegative() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
            service.createAlert(UUID.randomUUID(), UUID.randomUUID(), "bloodPressure", -10.0, "CRITICAL")
        );
        assertTrue(ex.getMessage().contains("triggeredValue must be greater than 0"));
    }

    @Test
    void createAlert_succeedsWithValidThreshold() {
        Alert alert = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 140.0, "WARNING"
        );
        assertNotNull(alert);
        assertEquals(140.0, alert.getTriggeredValue());
        assertEquals("WARNING", alert.getSeverity());
    }

    @Test
    void createAlert_succeedsWithMinimalPositiveThreshold() {
        Alert alert = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "oxygenLevel", 0.1, "WARNING"
        );
        assertEquals(0.1, alert.getTriggeredValue());
    }

    @Test
    void createAlert_succeedsWithHighThresholdValue() {
        Alert alert = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "bloodPressure", 999.9, "CRITICAL"
        );
        assertEquals(999.9, alert.getTriggeredValue());
    }

    // ── 2. Required field validation ──────────────────────────────────────

    @Test
    void createAlert_throwsWhenVitalTypeIsNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
            service.createAlert(UUID.randomUUID(), UUID.randomUUID(), null, 120.0, "WARNING")
        );
        assertTrue(ex.getMessage().contains("vitalType is required"));
    }

    @Test
    void createAlert_throwsWhenVitalTypeIsBlank() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
            service.createAlert(UUID.randomUUID(), UUID.randomUUID(), "   ", 120.0, "WARNING")
        );
        assertTrue(ex.getMessage().contains("vitalType is required"));
    }

    @Test
    void createAlert_throwsWhenSeverityIsNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
            service.createAlert(UUID.randomUUID(), UUID.randomUUID(), "heartRate", 120.0, null)
        );
        assertTrue(ex.getMessage().contains("severity is required"));
    }

    @Test
    void createAlert_throwsWhenSeverityIsBlank() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
            service.createAlert(UUID.randomUUID(), UUID.randomUUID(), "heartRate", 120.0, "")
        );
        assertTrue(ex.getMessage().contains("severity is required"));
    }

    // ── 3. State transitions ──────────────────────────────────────────────

    @Test
    void triggerAlert_changesStatusAwayFromOpen() {
        Alert alert = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 150.0, "CRITICAL"
        );
        Alert triggered = service.triggerAlert(alert.getAlertId());
        // Status should no longer be OPEN after triggering
        assertNotEquals("OPEN", triggered.getStatus());
    }

    @Test
    void acknowledgeAlert_afterTrigger_changesStatusToAcknowledged() {
        Alert alert = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 140.0, "WARNING"
        );
        service.triggerAlert(alert.getAlertId());
        Alert acknowledged = service.acknowledgeAlert(alert.getAlertId(), UUID.randomUUID());
        assertEquals("ACKNOWLEDGED", acknowledged.getStatus());
    }

    @Test
    void closeAlert_changesStatusToClosed() {
        Alert alert = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "oxygenLevel", 85.0, "CRITICAL"
        );
        service.triggerAlert(alert.getAlertId());
        service.acknowledgeAlert(alert.getAlertId(), UUID.randomUUID());
        Alert closed = service.closeAlert(alert.getAlertId());
        assertEquals("CLOSED", closed.getStatus());
    }

    @Test
    void fullAlertLifecycle_createdToClosedSuccessfully() {
        Alert alert = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "temperature", 39.5, "WARNING"
        );
        assertNotNull(alert.getAlertId());

        // Trigger — status changes away from OPEN
        Alert triggered = service.triggerAlert(alert.getAlertId());
        assertNotEquals("OPEN", triggered.getStatus());

        // Acknowledge
        Alert acknowledged = service.acknowledgeAlert(alert.getAlertId(), UUID.randomUUID());
        assertEquals("ACKNOWLEDGED", acknowledged.getStatus());

        // Close
        Alert closed = service.closeAlert(alert.getAlertId());
        assertEquals("CLOSED", closed.getStatus());
    }

    // ── 4. Retrieval ──────────────────────────────────────────────────────

    @Test
    void getAlertById_returnsCorrectAlert() {
        Alert alert = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 130.0, "WARNING"
        );
        Alert fetched = service.getAlertById(alert.getAlertId());
        assertEquals(alert.getAlertId(), fetched.getAlertId());
    }

    @Test
    void getAlertById_throwsWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () ->
            service.getAlertById(UUID.randomUUID())
        );
    }

    @Test
    void getAllAlerts_returnsAllCreatedAlerts() {
        service.createAlert(UUID.randomUUID(), UUID.randomUUID(), "heartRate", 140.0, "WARNING");
        service.createAlert(UUID.randomUUID(), UUID.randomUUID(), "bloodPressure", 180.0, "CRITICAL");
        service.createAlert(UUID.randomUUID(), UUID.randomUUID(), "oxygenLevel", 88.0, "WARNING");

        List<Alert> alerts = service.getAllAlerts();
        assertEquals(3, alerts.size());
    }

    @Test
    void getAllAlerts_returnsEmptyListWhenNoAlerts() {
        List<Alert> alerts = service.getAllAlerts();
        assertTrue(alerts.isEmpty());
    }

    // ── 5. Delete ─────────────────────────────────────────────────────────

    @Test
    void deleteAlert_removesAlertFromRepository() {
        Alert alert = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 140.0, "WARNING"
        );
        service.deleteAlert(alert.getAlertId());
        assertThrows(ResourceNotFoundException.class, () ->
            service.getAlertById(alert.getAlertId())
        );
    }

    @Test
    void deleteAlert_throwsWhenAlertDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () ->
            service.deleteAlert(UUID.randomUUID())
        );
    }

    // ── 6. Multiple patients / isolation ─────────────────────────────────

    @Test
    void multipleAlerts_forDifferentPatients_areStoredIndependently() {
        UUID patient1 = UUID.randomUUID();
        UUID patient2 = UUID.randomUUID();

        Alert alert1 = service.createAlert(patient1, UUID.randomUUID(), "heartRate", 140.0, "WARNING");
        Alert alert2 = service.createAlert(patient2, UUID.randomUUID(), "bloodPressure", 190.0, "CRITICAL");

        assertNotEquals(alert1.getAlertId(), alert2.getAlertId());
        assertEquals(2, service.getAllAlerts().size());
    }

    @Test
    void triggeringOneAlert_doesNotAffectOtherAlerts() {
        Alert alert1 = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 140.0, "WARNING"
        );
        Alert alert2 = service.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "oxygenLevel", 85.0, "CRITICAL"
        );

        service.triggerAlert(alert1.getAlertId());

        Alert fetched2 = service.getAlertById(alert2.getAlertId());
        assertNotEquals("ACKNOWLEDGED", fetched2.getStatus());
        assertNotEquals("CLOSED", fetched2.getStatus());
    }
}