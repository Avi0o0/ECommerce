package com.ecom.userservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.ecom.userservice.controller.AuthController.ApiMessage;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.repository.UserAccountRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
public class UserAdminController {

	private static final Logger logger = LoggerFactory.getLogger(UserAdminController.class);

	private final UserAccountRepository userRepo;
	private final PasswordEncoder passwordEncoder;

	public UserAdminController(UserAccountRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/users")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public List<UserAccount> listUsers() {
		logger.info("Request to list all users received");
		
		try {
			List<UserAccount> users = userRepo.findAll();
			logger.info("Successfully retrieved {} users from database", users.size());
			logger.debug("User list: {}", users.stream().map(UserAccount::getUsername).toList());
			return users;
		} catch (Exception e) {
			logger.error("Failed to retrieve user list. Error: {}", e.getMessage());
			throw e;
		}
	}

	@GetMapping("/users/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<UserAccount> getUser(@PathVariable Long id) {
		logger.info("Request to get user by ID: {}", id);
		
		try {
			return userRepo.findById(id).map(user -> {
				logger.info("Successfully retrieved user: {} with ID: {}", user.getUsername(), id);
				return ResponseEntity.ok(user);
			}).orElseGet(() -> {
				logger.warn("User not found with ID: {}", id);
				return ResponseEntity.notFound().build();
			});
		} catch (Exception e) {
			logger.error("Failed to retrieve user with ID: {}. Error: {}", id, e.getMessage());
			throw e;
		}
	}

	@PutMapping("/users/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<UserAccount> updatePassword(@PathVariable Long id, @RequestBody @Valid UpdatePassword req) {
		logger.info("Request to update password for user ID: {}", id);
		
		try {
			return userRepo.findById(id).map(u -> {
				logger.debug("Updating password for user: {}", u.getUsername());
				u.setPasswordHash(passwordEncoder.encode(req.password()));
				UserAccount savedUser = userRepo.save(u);
				logger.info("Password successfully updated for user: {} with ID: {}", savedUser.getUsername(), id);
				return ResponseEntity.ok(savedUser);
			}).orElseGet(() -> {
				logger.warn("User not found for password update with ID: {}", id);
				return ResponseEntity.notFound().build();
			});
		} catch (Exception e) {
			logger.error("Failed to update password for user ID: {}. Error: {}", id, e.getMessage());
			throw e;
		}
	}

	@DeleteMapping("/users/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiMessage> deleteUser(@PathVariable Long id) {
		logger.info("Request to delete user with ID: {}", id);
		
		try {
			if (userRepo.existsById(id)) {
				logger.debug("User exists with ID: {}, proceeding with deletion", id);
				userRepo.deleteById(id);
				logger.info("User successfully deleted with ID: {}", id);
				return ResponseEntity.ok(new ApiMessage("User with ID " + id + " has been successfully deleted"));
			} else {
				logger.warn("User not found for deletion with ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new ApiMessage("User with ID " + id + " not found. Cannot delete non-existent user."));
			}
		} catch (Exception e) {
			logger.error("Failed to delete user with ID: {}. Error: {}", id, e.getMessage());
			throw e;
		}
	}

	// Password verification endpoint
	@PostMapping("/users/{id}/verify-password")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PasswordVerificationResponse> verifyPassword(@PathVariable Long id, @RequestBody @Valid PasswordVerificationRequest request) {
		logger.info("Request to verify password for user ID: {}", id);
		
		try {
			return userRepo.findById(id).map(user -> {
				logger.debug("Verifying password for user: {}", user.getUsername());
				boolean matches = passwordEncoder.matches(request.password(), user.getPasswordHash());
				logger.info("Password verification result for user {}: {}", user.getUsername(), matches ? "MATCH" : "NO MATCH");
				return ResponseEntity.ok(new PasswordVerificationResponse(matches, user.getUsername()));
			}).orElseGet(() -> {
				logger.warn("User not found for password verification with ID: {}", id);
				return ResponseEntity.notFound().build();
			});
		} catch (Exception e) {
			logger.error("Failed to verify password for user ID: {}. Error: {}", id, e.getMessage());
			throw e;
		}
	}

	public static record UpdatePassword(@NotBlank String password) {}
	public static record PasswordVerificationRequest(@NotBlank String password) {}
	public static record PasswordVerificationResponse(boolean matches, String username) {}
}


