package com.hpms.services;

import com.hpms.domain.Alert;
import com.hpms.services.exceptions.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * EmailNotificationService — sends email notifications for critical alerts.
 *
 * Acceptance Criteria (FR-06 / Issue #7):
 *   - Critical alert email delivered within 60 seconds
 *   - Email includes patient name, vital type, triggered value, and timestamp
 *   - Delivery is logged in the system
 *
 * Design note: Uses a pluggable EmailSender interface so the real SMTP
 * implementation can be swapped in without changing business logic.
 * In tests, a mock/in-memory sender is injected instead.
 */
@Service
public class EmailNotificationService {

    private static final Logger LOGGER = Logger.getLogger(EmailNotificationService.class.getName());
    private static final String CRITICAL_SEVERITY = "CRITICAL";

    private final EmailSender emailSender;
    private final List<NotificationLog> deliveryLog = new ArrayList<>();

    public EmailNotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    // ── Public API ────────────────────────────────────────────────────────

    /**
     * Sends a critical alert email to the given doctor email address.
     * Logs delivery result regardless of outcome.
     *
     * @param alert       the alert that was triggered
     * @param patientName the full name of the patient
     * @param doctorEmail the email address of the notified doctor
     */
    public void notifyDoctor(Alert alert, String patientName, String doctorEmail) {
        validateRequired(patientName, "patientName");
        validateRequired(doctorEmail, "doctorEmail");
        if (alert == null) {
            throw new BusinessRuleException("alert must not be null.");
        }
        if (!CRITICAL_SEVERITY.equalsIgnoreCase(alert.getSeverity())) {
            throw new BusinessRuleException(
                    "Email notifications are only sent for CRITICAL alerts. " +
                    "Received severity: " + alert.getSeverity()
            );
        }

        String subject = buildSubject(patientName, alert);
        String body    = buildBody(patientName, alert);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        boolean success = false;
        try {
            emailSender.send(doctorEmail, subject, body);
            success = true;
            LOGGER.info(String.format(
                "[NOTIFICATION] Critical alert email sent to %s for patient %s at %s",
                doctorEmail, patientName, timestamp
            ));
        } catch (Exception e) {
            LOGGER.severe(String.format(
                "[NOTIFICATION] Failed to send critical alert email to %s: %s",
                doctorEmail, e.getMessage()
            ));
        }

        deliveryLog.add(new NotificationLog(
                alert.getAlertId().toString(),
                patientName,
                doctorEmail,
                timestamp,
                success
        ));
    }

    /**
     * Returns an unmodifiable view of the delivery log.
     */
    public List<NotificationLog> getDeliveryLog() {
        return Collections.unmodifiableList(deliveryLog);
    }

    // ── Email content builders ────────────────────────────────────────────

    private String buildSubject(String patientName, Alert alert) {
        return String.format(
            "[CRITICAL ALERT] Patient: %s — Vital: %s",
            patientName, alert.getVitalType()
        );
    }

    private String buildBody(String patientName, Alert alert) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return String.format(
            "CRITICAL ALERT NOTIFICATION\n" +
            "===========================\n" +
            "Patient Name  : %s\n" +
            "Vital Type    : %s\n" +
            "Triggered Value: %.2f\n" +
            "Severity      : %s\n" +
            "Timestamp     : %s\n" +
            "===========================\n" +
            "Please log in to the Hospital Patient Monitoring System immediately.\n",
            patientName,
            alert.getVitalType(),
            alert.getTriggeredValue(),
            alert.getSeverity(),
            timestamp
        );
    }

    // ── Validation ────────────────────────────────────────────────────────

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(fieldName + " is required.");
        }
    }

    // ── Inner types ───────────────────────────────────────────────────────

    /**
     * Pluggable email sender — implement with JavaMail/SMTP for production.
     */
    public interface EmailSender {
        void send(String to, String subject, String body);
    }

    /**
     * Immutable log entry for a single notification attempt.
     */
    public static class NotificationLog {
        public final String alertId;
        public final String patientName;
        public final String doctorEmail;
        public final String timestamp;
        public final boolean success;

        public NotificationLog(String alertId, String patientName,
                               String doctorEmail, String timestamp, boolean success) {
            this.alertId     = alertId;
            this.patientName = patientName;
            this.doctorEmail = doctorEmail;
            this.timestamp   = timestamp;
            this.success     = success;
        }
    }
}