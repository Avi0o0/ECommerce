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
import com.ecom.userservice.dto.SuccessResponse;
import com.ecom.userservice.dto.UserListResponse;
import com.ecom.userservice.dto.UpdatePasswordRequest;
import com.ecom.userservice.dto.VerifyPasswordRequest;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.exception.UserNotFoundException;
import com.ecom.userservice.exception.InvalidPasswordException;
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
    public ResponseEntity<UserListResponse> listUsers() {
        logger.info("Request to list all users received");
        List<UserAccount> users = userRepo.findAll();
        logger.info("Successfully retrieved {} users from database", users.size());
        
        UserListResponse response = new UserListResponse(
            200, 
            "Users retrieved successfully", 
            users.size(), 
            users
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> getUser(@PathVariable Long id) {
        logger.info("Request to get user by ID: {}", id);

        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });
        
        logger.info("Successfully retrieved user: {} with ID: {}", user.getUsername(), id);
        SuccessResponse response = new SuccessResponse(200, "User retrieved successfully", user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteUser(@PathVariable Long id) {
        logger.info("Request to delete user with ID: {}", id);
        
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });
        
        userRepo.delete(user);
        logger.info("User {} deleted successfully with ID: {}", user.getUsername(), id);
        SuccessResponse response = new SuccessResponse(200, "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse> updatePassword(@PathVariable Long id, 
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
            throw new InvalidPasswordException("Current password is incorrect");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        
        logger.info("Password updated successfully for user: {} with ID: {}", user.getUsername(), id);
        SuccessResponse response = new SuccessResponse(200, "Password updated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/verify-password")
    public ResponseEntity<SuccessResponse> verifyPassword(@PathVariable Long id, 
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
            SuccessResponse response = new SuccessResponse(200, "Password is valid");
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Password verification failed for user ID: {}", id);
            throw new InvalidPasswordException("Password is incorrect");
        }
    }
}