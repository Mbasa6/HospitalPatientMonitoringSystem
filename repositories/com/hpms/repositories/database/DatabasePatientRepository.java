package com.hpms.repositories.database;

import com.hpms.domain.Patient;
import com.hpms.repositories.PatientRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DatabasePatientRepository implements PatientRepository {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE_BYTES = 32;
    private static final int GCM_IV_SIZE_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final String KEY_SEED = "HospitalPatientMonitoringSystem:patient-data-encryption:v1";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final Map<UUID, String> encryptedPatients = new ConcurrentHashMap<>();

    @Override
    public void save(Patient entity) {
        encryptedPatients.put(entity.getPatientId(), encrypt(entity));
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return Optional.ofNullable(encryptedPatients.get(id))
                .map(this::decrypt);
    }

    @Override
    public List<Patient> findAll() {
        return encryptedPatients.values().stream()
                .map(this::decrypt)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        encryptedPatients.remove(id);
    }

    private String encrypt(Patient patient) {
        try {
            byte[] plainBytes = serialize(patient);
            byte[] iv = new byte[GCM_IV_SIZE_BYTES];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] cipherBytes = cipher.doFinal(plainBytes);

            byte[] combined = new byte[iv.length + cipherBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherBytes, 0, combined, iv.length, cipherBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (GeneralSecurityException | IOException ex) {
            throw new IllegalStateException("Unable to encrypt patient record.", ex);
        }
    }

    private Patient decrypt(String encodedPayload) {
        try {
            byte[] combined = Base64.getDecoder().decode(encodedPayload.getBytes(StandardCharsets.UTF_8));
            byte[] iv = new byte[GCM_IV_SIZE_BYTES];
            byte[] cipherBytes = new byte[combined.length - GCM_IV_SIZE_BYTES];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_SIZE_BYTES);
            System.arraycopy(combined, GCM_IV_SIZE_BYTES, cipherBytes, 0, cipherBytes.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return deserialize(plainBytes);
        } catch (GeneralSecurityException | IOException | ClassNotFoundException ex) {
            throw new IllegalStateException("Unable to decrypt patient record.", ex);
        }
    }

    private byte[] serialize(Patient patient) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(patient);
        }
        return outputStream.toByteArray();
    }

    private Patient deserialize(byte[] plainBytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(plainBytes))) {
            return (Patient) objectInputStream.readObject();
        }
    }

    private SecretKeySpec getKey() throws GeneralSecurityException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(KEY_SEED.getBytes(StandardCharsets.UTF_8));
        byte[] aesKey = new byte[AES_KEY_SIZE_BYTES];
        System.arraycopy(keyBytes, 0, aesKey, 0, aesKey.length);
        return new SecretKeySpec(aesKey, "AES");
    }
}