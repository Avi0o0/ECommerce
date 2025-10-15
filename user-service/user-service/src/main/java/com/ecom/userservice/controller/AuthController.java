package com.ecom.userservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.userservice.dto.LoginRequest;
import com.ecom.userservice.dto.RegisterRequest;
import com.ecom.userservice.dto.SuccessResponse;
import com.ecom.userservice.dto.TokenResponse;
import com.ecom.userservice.dto.TokenValidationResponse;
import com.ecom.userservice.entity.Role;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.exception.InvalidCredentialsException;
import com.ecom.userservice.exception.UsernameAlreadyExistsException;
import com.ecom.userservice.repository.RoleRepository;
import com.ecom.userservice.repository.UserAccountRepository;
import com.ecom.userservice.security.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        logger.info("Login attempt for username: {}", request.username());

        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            String token = jwtService
                    .generateToken((org.springframework.security.core.userdetails.User) auth.getPrincipal());

            logger.info("Login successful for user: {}", request.username());
            return ResponseEntity.ok(new TokenResponse(token));
        } catch (BadCredentialsException e) {
            logger.warn("Login failed for user: {} - Invalid credentials", request.username());
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (Exception e) {
            logger.error("Login failed for user: {}. Error: {}", request.username(), e.getMessage(), e);
            throw new InvalidCredentialsException("Authentication failed");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenResponse request) {
        logger.info("Token validation request received");

        try {
            boolean isValid = jwtService.isTokenValid(request.token());
            if (isValid) {
                String username = jwtService.extractUsername(request.token());
                List<String> roles = jwtService.extractRoles(request.token());
                
                // Get userId from username
                Long userId = userRepo.findByUsername(username)
                        .map(user -> user.getId())
                        .orElse(null);
                
                logger.info("Token validation successful for user: {} (ID: {}) with roles: {}", username, userId, roles);
                
                TokenValidationResponse response = new TokenValidationResponse(
                    request.token(), username, userId, roles, true);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Token validation failed");
                TokenValidationResponse response = new TokenValidationResponse(
                    request.token(), null, null, null, false);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage(), e);
            TokenValidationResponse response = new TokenValidationResponse(
                request.token(), null, null, null, false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@RequestBody RegisterRequest request) {
        logger.info("User registration attempt for username: {}", request.username());

        try {
            if (userRepo.existsByUsername(request.username())) {
                logger.warn("Registration failed - username already exists: {}", request.username());
                throw new UsernameAlreadyExistsException("Username '" + request.username() + "' already exists");
            }

            UserAccount user = new UserAccount();
            user.setUsername(request.username());
            user.setPasswordHash(passwordEncoder.encode(request.password()));

            // Set default role as USER
            Role role = roleRepo.findByName(request.role()).orElseGet(() -> {
                Role r = new Role();
                r.setName(request.role());
                return roleRepo.save(r);
            });

            user.setRoles(java.util.Set.of(role));
            userRepo.save(user);

            logger.info("User registration successful for username: {}", request.username());
            SuccessResponse response = new SuccessResponse(201, "User registered successfully");
            return ResponseEntity.status(201).body(response);
        } catch (UsernameAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("User registration failed for username: {}. Error: {}", request.username(), e.getMessage(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }
}