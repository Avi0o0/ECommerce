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

import com.ecom.userservice.constants.UserServiceConstants;
import com.ecom.userservice.dto.SuccessResponse;
import com.ecom.userservice.dto.UpdatePasswordRequest;
import com.ecom.userservice.dto.UserListResponse;
import com.ecom.userservice.dto.VerifyPasswordRequest;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.exception.InvalidPasswordException;
import com.ecom.userservice.exception.PasswordVerificationFailedException;
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
    public ResponseEntity<UserListResponse> listUsers() {
        logger.info(UserServiceConstants.LOG_REQUEST_TO_LIST_ALL_USERS);
        List<UserAccount> users = userRepo.findAll();
        logger.info(UserServiceConstants.LOG_SUCCESSFULLY_RETRIEVED_USERS, users.size());
        
        UserListResponse response = new UserListResponse(
            200, 
            UserServiceConstants.USERS_RETRIEVED_SUCCESS_MESSAGE, 
            users.size(), 
            users
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> getUser(@PathVariable Long id) {
        logger.info(UserServiceConstants.LOG_REQUEST_TO_GET_USER_BY_ID, id);

        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn(UserServiceConstants.LOG_USER_NOT_FOUND_WITH_ID, id);
                    return new UserNotFoundException(UserServiceConstants.USER_NOT_FOUND_MESSAGE + id);
                });
        
        logger.info(UserServiceConstants.LOG_SUCCESSFULLY_RETRIEVED_USER, user.getUsername(), id);
        SuccessResponse response = new SuccessResponse(200, UserServiceConstants.USER_RETRIEVED_SUCCESS_MESSAGE, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteUser(@PathVariable Long id) {
        logger.info(UserServiceConstants.LOG_REQUEST_TO_DELETE_USER, id);
        
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn(UserServiceConstants.LOG_USER_NOT_FOUND_WITH_ID, id);
                    return new UserNotFoundException(UserServiceConstants.USER_NOT_FOUND_MESSAGE + id);
                });
        
        userRepo.delete(user);
        logger.info(UserServiceConstants.LOG_USER_DELETED_SUCCESSFULLY, user.getUsername(), id);
        SuccessResponse response = new SuccessResponse(200, UserServiceConstants.USER_DELETED_SUCCESS_MESSAGE);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse> updatePassword(@PathVariable Long id, 
                                                    @Valid @RequestBody UpdatePasswordRequest request) {
        logger.info(UserServiceConstants.LOG_REQUEST_TO_UPDATE_PASSWORD, id);
        
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn(UserServiceConstants.LOG_USER_NOT_FOUND_WITH_ID, id);
                    return new UserNotFoundException(UserServiceConstants.USER_NOT_FOUND_MESSAGE + id);
                });
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            logger.warn(UserServiceConstants.LOG_INVALID_CURRENT_PASSWORD, id);
            throw new InvalidPasswordException(UserServiceConstants.CURRENT_PASSWORD_INCORRECT_MESSAGE);
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        
        logger.info(UserServiceConstants.LOG_PASSWORD_UPDATED_SUCCESSFULLY, user.getUsername(), id);
        SuccessResponse response = new SuccessResponse(200, UserServiceConstants.PASSWORD_UPDATED_SUCCESS_MESSAGE);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/verify-password")
    public ResponseEntity<SuccessResponse> verifyPassword(@PathVariable Long id, 
                                                    @Valid @RequestBody VerifyPasswordRequest request) {
        logger.info(UserServiceConstants.LOG_REQUEST_TO_VERIFY_PASSWORD, id);
        
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn(UserServiceConstants.LOG_USER_NOT_FOUND_WITH_ID, id);
                    return new UserNotFoundException(UserServiceConstants.USER_NOT_FOUND_MESSAGE + id);
                });
        
        boolean isValid;
        try {
            isValid = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        } catch (Exception e) {
            logger.error(UserServiceConstants.LOG_PASSWORD_VERIFICATION_ERROR, id, e.getMessage(), e);
            throw new PasswordVerificationFailedException(UserServiceConstants.PASSWORD_VERIFICATION_SYSTEM_ERROR_MESSAGE, e);
        }
        
        if (isValid) {
            logger.info(UserServiceConstants.LOG_PASSWORD_VERIFICATION_SUCCESSFUL, user.getUsername(), id);
            SuccessResponse response = new SuccessResponse(200, UserServiceConstants.PASSWORD_IS_VALID_MESSAGE);
            return ResponseEntity.ok(response);
        } else {
            logger.warn(UserServiceConstants.LOG_PASSWORD_VERIFICATION_FAILED, id);
            throw new PasswordVerificationFailedException(UserServiceConstants.PASSWORD_VERIFICATION_FAILED_MESSAGE);
        }
    }
}