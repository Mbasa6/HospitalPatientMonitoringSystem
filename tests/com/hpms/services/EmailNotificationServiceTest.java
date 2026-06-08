package com.hpms.services;

import com.hpms.domain.Alert;
import com.hpms.repositories.inmemory.InMemoryAlertRepository;
import com.hpms.services.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EmailNotificationServiceTest — unit tests for critical alert email notifications.
 * Relates to: Issue #7 — Email notifications for critical alerts (FR-06)
 */
class EmailNotificationServiceTest {

    // ── In-memory mock email sender ───────────────────────────────────────

    static class MockEmailSender implements EmailNotificationService.EmailSender {
        final List<String> sentTo      = new ArrayList<>();
        final List<String> subjects    = new ArrayList<>();
        final List<String> bodies      = new ArrayList<>();
        boolean shouldFail             = false;

        @Override
        public void send(String to, String subject, String body) {
            if (shouldFail) throw new RuntimeException("SMTP unavailable");
            sentTo.add(to);
            subjects.add(subject);
            bodies.add(body);
        }
    }

    private AlertService              alertService;
    private MockEmailSender           mockSender;
    private EmailNotificationService  notificationService;

    @BeforeEach
    void setUp() {
        alertService         = new AlertService(new InMemoryAlertRepository());
        mockSender           = new MockEmailSender();
        notificationService  = new EmailNotificationService(mockSender);
    }

    // ── 1. Happy path ─────────────────────────────────────────────────────

    @Test
    void notifyDoctor_sendsCriticalAlertEmail() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 180.0, "CRITICAL"
        );

        notificationService.notifyDoctor(alert, "John Doe", "doctor@hospital.com");

        assertEquals(1, mockSender.sentTo.size());
        assertEquals("doctor@hospital.com", mockSender.sentTo.get(0));
    }

    @Test
    void notifyDoctor_emailSubjectContainsPatientNameAndVitalType() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 180.0, "CRITICAL"
        );

        notificationService.notifyDoctor(alert, "Jane Smith", "doctor@hospital.com");

        String subject = mockSender.subjects.get(0);
        assertTrue(subject.contains("Jane Smith"));
        assertTrue(subject.contains("heartRate"));
    }

    @Test
    void notifyDoctor_emailBodyContainsAllRequiredFields() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "bloodPressure", 190.0, "CRITICAL"
        );

        notificationService.notifyDoctor(alert, "Alice Brown", "doctor@hospital.com");

        String body = mockSender.bodies.get(0);
        assertTrue(body.contains("Alice Brown"),   "body should contain patient name");
        assertTrue(body.contains("bloodPressure"), "body should contain vital type");
        assertTrue(body.contains("190.00"),         "body should contain triggered value");
        assertTrue(body.contains("CRITICAL"),       "body should contain severity");
    }

    @Test
    void notifyDoctor_emailBodyContainsTimestamp() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "oxygenLevel", 85.0, "CRITICAL"
        );

        notificationService.notifyDoctor(alert, "Bob Green", "doctor@hospital.com");

        String body = mockSender.bodies.get(0);
        assertTrue(body.contains("Timestamp"), "body should contain a timestamp field");
    }

    // ── 2. Delivery log ───────────────────────────────────────────────────

    @Test
    void notifyDoctor_logsSuccessfulDelivery() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 175.0, "CRITICAL"
        );

        notificationService.notifyDoctor(alert, "John Doe", "doctor@hospital.com");

        List<EmailNotificationService.NotificationLog> log = notificationService.getDeliveryLog();
        assertEquals(1, log.size());
        assertTrue(log.get(0).success);
        assertEquals("John Doe", log.get(0).patientName);
        assertEquals("doctor@hospital.com", log.get(0).doctorEmail);
    }

    @Test
    void notifyDoctor_logsFailedDelivery_whenSenderThrows() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 175.0, "CRITICAL"
        );
        mockSender.shouldFail = true;

        notificationService.notifyDoctor(alert, "John Doe", "doctor@hospital.com");

        List<EmailNotificationService.NotificationLog> log = notificationService.getDeliveryLog();
        assertEquals(1, log.size());
        assertFalse(log.get(0).success);
    }

    @Test
    void notifyDoctor_multipleAlerts_allLogged() {
        Alert alert1 = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 180.0, "CRITICAL"
        );
        Alert alert2 = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "oxygenLevel", 82.0, "CRITICAL"
        );

        notificationService.notifyDoctor(alert1, "Patient A", "doctor1@hospital.com");
        notificationService.notifyDoctor(alert2, "Patient B", "doctor2@hospital.com");

        assertEquals(2, notificationService.getDeliveryLog().size());
        assertEquals(2, mockSender.sentTo.size());
    }

    // ── 3. Non-critical alerts blocked ───────────────────────────────────

    @Test
    void notifyDoctor_throwsForWarningAlert() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 120.0, "WARNING"
        );

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
            notificationService.notifyDoctor(alert, "John Doe", "doctor@hospital.com")
        );
        assertTrue(ex.getMessage().contains("CRITICAL"));
        assertEquals(0, mockSender.sentTo.size());
    }

    // ── 4. Input validation ───────────────────────────────────────────────

    @Test
    void notifyDoctor_throwsWhenAlertIsNull() {
        assertThrows(BusinessRuleException.class, () ->
            notificationService.notifyDoctor(null, "John Doe", "doctor@hospital.com")
        );
    }

    @Test
    void notifyDoctor_throwsWhenPatientNameIsNull() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 180.0, "CRITICAL"
        );
        assertThrows(BusinessRuleException.class, () ->
            notificationService.notifyDoctor(alert, null, "doctor@hospital.com")
        );
    }

    @Test
    void notifyDoctor_throwsWhenPatientNameIsBlank() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 180.0, "CRITICAL"
        );
        assertThrows(BusinessRuleException.class, () ->
            notificationService.notifyDoctor(alert, "   ", "doctor@hospital.com")
        );
    }

    @Test
    void notifyDoctor_throwsWhenDoctorEmailIsNull() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 180.0, "CRITICAL"
        );
        assertThrows(BusinessRuleException.class, () ->
            notificationService.notifyDoctor(alert, "John Doe", null)
        );
    }

    @Test
    void notifyDoctor_throwsWhenDoctorEmailIsBlank() {
        Alert alert = alertService.createAlert(
                UUID.randomUUID(), UUID.randomUUID(), "heartRate", 180.0, "CRITICAL"
        );
        assertThrows(BusinessRuleException.class, () ->
            notificationService.notifyDoctor(alert, "John Doe", "")
        );
    }
}