package com.hpms.repositories.database;

import com.hpms.domain.Patient;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabasePatientRepositoryTest {
    @Test
    void savesAndRestoresEncryptedPatientRecords() throws Exception {
        DatabasePatientRepository repository = new DatabasePatientRepository();
        Patient patient = new Patient("Lena", "Moyo", LocalDate.of(1999, 3, 10), "Flu");
        patient.register();
        repository.save(patient);

        Optional<Patient> restored = repository.findById(patient.getPatientId());

        assertTrue(restored.isPresent());
        assertEquals(patient.getPatientId(), restored.orElseThrow().getPatientId());
        assertEquals("Flu", restored.orElseThrow().getDiagnosis());
        assertEquals("ADMITTED", restored.orElseThrow().getStatus());

        Field storageField = DatabasePatientRepository.class.getDeclaredField("encryptedPatients");
        storageField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<?, String> storage = (Map<?, String>) storageField.get(repository);
        String storedCiphertext = storage.values().iterator().next();

        String plainSerialized = Base64.getEncoder().encodeToString(serialize(patient));

        assertNotEquals(plainSerialized, storedCiphertext);
        assertFalse(storedCiphertext.contains("Lena"));
        assertFalse(storedCiphertext.contains("Moyo"));
        assertFalse(storedCiphertext.contains("Flu"));
    }

    @Test
    void deleteRemovesEncryptedRecord() {
        DatabasePatientRepository repository = new DatabasePatientRepository();
        Patient patient = new Patient("Ava", "Smith", LocalDate.of(1990, 1, 1), "Asthma");

        repository.save(patient);
        repository.delete(patient.getPatientId());

        assertTrue(repository.findById(patient.getPatientId()).isEmpty());
        assertTrue(repository.findAll().isEmpty());
    }

    private byte[] serialize(Patient patient) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(patient);
        }
        return outputStream.toByteArray();
    }
}