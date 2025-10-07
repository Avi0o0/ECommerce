package com.ecom.userservice.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.userservice.entity.Role;
import com.ecom.userservice.entity.RoleName;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.repository.RoleRepository;
import com.ecom.userservice.repository.UserAccountRepository;
import com.ecom.userservice.security.JwtService;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final UserAccountRepository userRepo;
	private final RoleRepository roleRepo;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
				PasswordEncoder passwordEncoder, UserAccountRepository userRepo, RoleRepository roleRepo) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
	}

	public static record LoginRequest(@NotBlank String username, @NotBlank String password) {}
	public static record TokenResponse(String token) {}
	public static record ApiMessage(String message) {}
    public static record ValidateResponse(String username, java.util.List<String> roles, long expiresAt) {}
	public static record RegisterRequest(@NotBlank String username, @NotBlank String password, RoleName role) {}

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		String token = jwtService.generateToken((org.springframework.security.core.userdetails.User) auth.getPrincipal());
		return ResponseEntity.ok(new TokenResponse(token));
	}

	@PostMapping("/validate")
	public ResponseEntity<ValidateResponse> validate(@RequestBody TokenResponse token) {
		var username = jwtService.extractUsername(token.token());
		var roles = jwtService.extractRoles(token.token());
		var exp = jwtService.extractExpiration(token.token());
		return ResponseEntity.ok(new ValidateResponse(username, roles, exp.toInstant().getEpochSecond()));
	}

	@PostMapping("/register")
	public ResponseEntity<ApiMessage> register(@RequestBody RegisterRequest request) {
		if (userRepo.existsByUsername(request.username())) {
			return ResponseEntity.badRequest().body(new ApiMessage("username already exists"));
		}
		UserAccount user = new UserAccount();
		user.setUsername(request.username());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		RoleName roleName = request.role() == null ? RoleName.USER : request.role();
		Role role = roleRepo.findByName(roleName).orElseGet(() -> {
			Role r = new Role();
			r.setName(roleName);
			return roleRepo.save(r);
		});
		user.setRoles(Set.of(role));
		userRepo.save(user);
		return ResponseEntity.ok(new ApiMessage("registered"));
	}
}


