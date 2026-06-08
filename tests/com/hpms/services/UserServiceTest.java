package com.hpms.services;

import com.hpms.domain.User;
import com.hpms.repositories.inmemory.InMemoryUserRepository;
import com.hpms.services.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import com.hpms.security.JwtUtil;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    @Test
    void deactivateCannotRunTwice() {
        JwtUtil jwtUtil = new JwtUtil();
        UserService service = new UserService(new InMemoryUserRepository(), jwtUtil);
        User user = service.createUser("Mia", "N", "mia@hpms.org", "password123", "NURSE", UUID.randomUUID());

        service.deactivateUser(user.getUserId());
        assertFalse(service.getUserById(user.getUserId()).isActive());

        assertThrows(BusinessRuleException.class, () -> service.deactivateUser(user.getUserId()));
    }
}