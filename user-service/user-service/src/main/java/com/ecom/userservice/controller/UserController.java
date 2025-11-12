package com.ecom.userservice.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.userservice.constants.UserServiceConstants;
import com.ecom.userservice.dto.GlobalErrorResponse;
import com.ecom.userservice.dto.OrderResponse;
import com.ecom.userservice.dto.OrderSummaryResponse;
import com.ecom.userservice.dto.SuccessResponse;
import com.ecom.userservice.dto.UpdatePasswordRequest;
import com.ecom.userservice.dto.UserListResponse;
import com.ecom.userservice.dto.UserResponse;
import com.ecom.userservice.dto.VerifyPasswordRequest;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.exception.PasswordVerificationFailedException;
import com.ecom.userservice.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<UserListResponse> listUsers() {
		logger.info(UserServiceConstants.LOG_REQUEST_TO_LIST_ALL_USERS);
		List<UserAccount> users = userService.listUsers();
		logger.info(UserServiceConstants.LOG_SUCCESSFULLY_RETRIEVED_USERS, users.size());

		// Map to UserResponse to avoid exposing sensitive fields
		List<UserResponse> dtoUsers = users.stream().map(u -> {
			UserResponse r = new UserResponse();
			r.setId(u.getId());
			r.setUsername(u.getUsername());
			r.setFirstName(u.getFirstName());
			r.setLastName(u.getLastName());
			r.setEmail(u.getEmail());
			r.setAddress(u.getAddress());
			r.setRoles(u.getRoles().stream().map(role -> role.getName().name())
					.collect(java.util.stream.Collectors.toSet()));
			return r;
		}).toList();

		UserListResponse response = new UserListResponse(200, UserServiceConstants.USERS_RETRIEVED_SUCCESS_MESSAGE,
				dtoUsers.size(), dtoUsers);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse> getUser(@PathVariable String id) {
		logger.info(UserServiceConstants.LOG_REQUEST_TO_GET_USER_BY_ID, id);

		UUID userId = validateUserID(id);
	
		UserAccount user = userService.getUserById(userId);
		logger.info(UserServiceConstants.LOG_SUCCESSFULLY_RETRIEVED_USER, user.getUsername(), id);

		UserResponse r = new UserResponse();
		r.setId(user.getId());
		r.setUsername(user.getUsername());
		r.setFirstName(user.getFirstName());
		r.setLastName(user.getLastName());
		r.setEmail(user.getEmail());
		r.setAddress(user.getAddress());
		r.setRoles(user.getRoles().stream().map(role -> role.getName().name())
				.collect(java.util.stream.Collectors.toSet()));

		SuccessResponse response = new SuccessResponse(200, UserServiceConstants.USER_RETRIEVED_SUCCESS_MESSAGE, r);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse> deleteUser(@PathVariable String id) {
		logger.info(UserServiceConstants.LOG_REQUEST_TO_DELETE_USER, id);

		UUID userId = validateUserID(id);
		
		userService.deleteUser(userId);
		logger.info(UserServiceConstants.LOG_USER_DELETED_SUCCESSFULLY, id, id);
		SuccessResponse response = new SuccessResponse(200, UserServiceConstants.USER_DELETED_SUCCESS_MESSAGE);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<SuccessResponse> updatePassword(@PathVariable String id,
			@Valid @RequestBody UpdatePasswordRequest request) {
		logger.info(UserServiceConstants.LOG_REQUEST_TO_UPDATE_PASSWORD, id);

		UUID userId = validateUserID(id);
		
		userService.updatePassword(userId, request);
		logger.info(UserServiceConstants.LOG_PASSWORD_UPDATED_SUCCESSFULLY, id, id);
		SuccessResponse response = new SuccessResponse(200, UserServiceConstants.PASSWORD_UPDATED_SUCCESS_MESSAGE);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{id}/verify-password")
	public ResponseEntity<SuccessResponse> verifyPassword(@PathVariable String id,
			@Valid @RequestBody VerifyPasswordRequest request) {
		logger.info(UserServiceConstants.LOG_REQUEST_TO_VERIFY_PASSWORD, id);

		UUID userId = validateUserID(id);

		boolean isValid = userService.verifyPassword(userId, request);
		if (isValid) {
			UserAccount user = userService.getUserById(userId);
			logger.info(UserServiceConstants.LOG_PASSWORD_VERIFICATION_SUCCESSFUL, user.getUsername(), id);
			SuccessResponse response = new SuccessResponse(200, UserServiceConstants.PASSWORD_IS_VALID_MESSAGE);
			return ResponseEntity.ok(response);
		} else {
			logger.warn(UserServiceConstants.LOG_PASSWORD_VERIFICATION_FAILED, id);
			throw new PasswordVerificationFailedException(UserServiceConstants.PASSWORD_VERIFICATION_FAILED_MESSAGE);
		}
	}

	@GetMapping("/{id}/orders")
	public ResponseEntity<Object> getUserOrders(@PathVariable String id,
			@RequestHeader("Authorization") String authorization) {
		// Ensure the requester is authenticated and has ROLE_USER
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getAuthorities().stream().noneMatch(a -> "ROLE_USER".equals(a.getAuthority()))) {
			return ResponseEntity.status(403)
					.body(new GlobalErrorResponse(403, "User role required", "Only users can access this endpoint"));
		}

		String username = auth.getName();

		UUID userId = validateUserID(id);

		try {
			List<OrderSummaryResponse> orders = userService.getUserOrders(userId, authorization, username);
			SuccessResponse response = new SuccessResponse(200, "User orders retrieved successfully", orders);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException iae) {
			return ResponseEntity.status(403).body(new GlobalErrorResponse(403, "Forbidden", iae.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(500)
					.body(new GlobalErrorResponse(500, "Failed to retrieve orders", e.getMessage()));
		}
	}

	@GetMapping("/{id}/orders/{orderId}")
	public ResponseEntity<Object> getOrderDetails(@PathVariable String id, @PathVariable Long orderId,
			@RequestHeader("Authorization") String authorization) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getAuthorities().stream().noneMatch(a -> "ROLE_USER".equals(a.getAuthority()))) {
			return ResponseEntity.status(403)
					.body(new GlobalErrorResponse(403, "User role required", "Only users can access this endpoint"));
		}

		UUID userId = validateUserID(id);

		String username = auth.getName();
		try {
			OrderResponse order = userService.getOrderDetails(userId, orderId, authorization, username);
			if (order == null) {
				return ResponseEntity.status(404)
						.body(new GlobalErrorResponse(404, "Order not found", "Order not found with id: " + orderId));
			}
			SuccessResponse response = new SuccessResponse(200, "Order retrieved successfully", order);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException iae) {
			return ResponseEntity.status(403).body(new GlobalErrorResponse(403, "Forbidden", iae.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(500)
					.body(new GlobalErrorResponse(500, "Failed to retrieve order", e.getMessage()));
		}
	}
	
	public UUID validateUserID(String id) {
		UUID userId = null;
		try {
			userId = UUID.fromString(id);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid ID");
		}
		return userId;
	}
}