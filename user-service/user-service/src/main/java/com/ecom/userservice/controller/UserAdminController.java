package com.ecom.userservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.repository.UserAccountRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
public class UserAdminController {

	private final UserAccountRepository userRepo;
	private final PasswordEncoder passwordEncoder;

	public UserAdminController(UserAccountRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/users")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public List<UserAccount> listUsers() {
		return userRepo.findAll();
	}

	@GetMapping("/users/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<UserAccount> getUser(@PathVariable Long id) {
		return userRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/users/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<UserAccount> updatePassword(@PathVariable Long id, @RequestBody @Valid UpdatePassword req) {
		return userRepo.findById(id).map(u -> {
			u.setPasswordHash(passwordEncoder.encode(req.password()));
			return ResponseEntity.ok(userRepo.save(u));
		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/users/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		if (userRepo.existsById(id)) {
			userRepo.deleteById(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	// Password verification endpoint
	@PostMapping("/users/{id}/verify-password")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PasswordVerificationResponse> verifyPassword(@PathVariable Long id, @RequestBody @Valid PasswordVerificationRequest request) {
		return userRepo.findById(id).map(user -> {
			boolean matches = passwordEncoder.matches(request.password(), user.getPasswordHash());
			return ResponseEntity.ok(new PasswordVerificationResponse(matches, user.getUsername()));
		}).orElse(ResponseEntity.notFound().build());
	}

	public static record UpdatePassword(@NotBlank String password) {}
	public static record PasswordVerificationRequest(@NotBlank String password) {}
	public static record PasswordVerificationResponse(boolean matches, String username) {}
}


