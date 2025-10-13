package com.ecom.userservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.userservice.dto.ApiMessage;
import com.ecom.userservice.dto.UpdatePasswordRequest;
import com.ecom.userservice.dto.VerifyPasswordRequest;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.exception.UserNotFoundException;
import com.ecom.userservice.repository.UserAccountRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserAccountRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserAccountRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserAccount> listUsers() {
        logger.info("Request to list all users received");
        List<UserAccount> users = userRepo.findAll();
        logger.info("Successfully retrieved {} users from database", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccount> getUser(@PathVariable Long id) {
        logger.info("Request to get user by ID: {}", id);

        return userRepo.findById(id).map(user -> {
            logger.info("Successfully retrieved user: {} with ID: {}", user.getUsername(), id);
            return ResponseEntity.ok(user);
        }).orElseGet(() -> {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessage> deleteUser(@PathVariable Long id) {
        logger.info("Request to delete user with ID: {}", id);
        
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });
        
        userRepo.delete(user);
        logger.info("User {} deleted successfully with ID: {}", user.getUsername(), id);
        return ResponseEntity.ok(new ApiMessage("User deleted successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiMessage> updatePassword(@PathVariable Long id, 
                                                    @Valid @RequestBody UpdatePasswordRequest request) {
        logger.info("Request to update password for user ID: {}", id);
        
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            logger.warn("Invalid current password for user ID: {}", id);
            return ResponseEntity.badRequest().body(new ApiMessage("Invalid current password"));
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        
        logger.info("Password updated successfully for user: {} with ID: {}", user.getUsername(), id);
        return ResponseEntity.ok(new ApiMessage("Password updated successfully"));
    }

    @PostMapping("/{id}/verify-password")
    public ResponseEntity<ApiMessage> verifyPassword(@PathVariable Long id, 
                                                    @Valid @RequestBody VerifyPasswordRequest request) {
        logger.info("Request to verify password for user ID: {}", id);
        
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });
        
        boolean isValid = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        
        if (isValid) {
            logger.info("Password verification successful for user: {} with ID: {}", user.getUsername(), id);
            return ResponseEntity.ok(new ApiMessage("Password is valid"));
        } else {
            logger.warn("Password verification failed for user ID: {}", id);
            return ResponseEntity.badRequest().body(new ApiMessage("Invalid password"));
        }
    }
}