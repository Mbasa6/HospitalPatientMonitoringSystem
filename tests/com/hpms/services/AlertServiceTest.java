package com.hpms.services;

import com.hpms.domain.Alert;
import com.hpms.repositories.inmemory.InMemoryAlertRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlertServiceTest {
    @Test
    void acknowledgeOpenAlert() {
        AlertService service = new AlertService(new InMemoryAlertRepository());
        Alert alert = service.createAlert(UUID.randomUUID(), UUID.randomUUID(), "heartRate", 140.0, "WARNING");

        service.triggerAlert(alert.getAlertId());
        Alert acknowledged = service.acknowledgeAlert(alert.getAlertId(), UUID.randomUUID());

        assertEquals("ACKNOWLEDGED", acknowledged.getStatus());
    }
}