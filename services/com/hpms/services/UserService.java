package com.hpms.services;

import com.hpms.domain.User;
import com.hpms.repositories.UserRepository;
import com.hpms.services.exceptions.BusinessRuleException;
import com.hpms.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final com.hpms.security.JwtUtil jwtUtil; 

    public UserService(UserRepository userRepository, com.hpms.security.JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public User createUser(String firstName,
                           String lastName,
                           String email,
                           String password,
                           String role,
                           UUID wardId) {
        validateRequired(firstName, "firstName");
        validateRequired(lastName, "lastName");
        validateRequired(email, "email");
        validateRequired(role, "role");
        validateRequired(password, "password");

        User user = new User(firstName, lastName, email, password, role, wardId);
        userRepository.save(user);
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public User resetPassword(UUID id, String newPassword) {
        User user = getUserById(id);
        user.resetPassword(newPassword);
        userRepository.save(user);
        return user;
    }

    public User deactivateUser(UUID id) {
        User user = getUserById(id);
        if (!user.isActive()) {
            throw new BusinessRuleException("User is already inactive.");
        }
        user.deactivate();
        userRepository.save(user);
        return user;
    }

    public void deleteUser(UUID id) {
        getUserById(id);
        userRepository.delete(id);
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(fieldName + " is required.");
        }
    }
    public String authenticateAndGenerateToken(String email, String password) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        // Use their existing domain logic to validate password and update login attempts
        user.login(email, password);
        userRepository.save(user);

        // Return the real JWT instead of the dummy domain token
        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }

}