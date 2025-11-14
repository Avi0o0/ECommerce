package com.ecom.userservice.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ecom.userservice.client.OrderServiceClient;
import com.ecom.userservice.constants.UserServiceConstants;
import com.ecom.userservice.dto.BlackListRequest;
import com.ecom.userservice.dto.OrderResponse;
import com.ecom.userservice.dto.OrderSummaryResponse;
import com.ecom.userservice.dto.UpdatePasswordRequest;
import com.ecom.userservice.dto.VerifyPasswordRequest;
import com.ecom.userservice.entity.BlackListToken;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.exception.InvalidPasswordException;
import com.ecom.userservice.exception.PasswordVerificationFailedException;
import com.ecom.userservice.exception.UserNotFoundException;
import com.ecom.userservice.repository.BlackListTokenRepo;
import com.ecom.userservice.repository.UserAccountRepository;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserAccountRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final OrderServiceClient orderClient;
	private final BlackListTokenRepo blackListTokenRepo;

	public UserService(UserAccountRepository userRepo, PasswordEncoder passwordEncoder, OrderServiceClient orderClient,
			BlackListTokenRepo blackListTokenRepo) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.orderClient = orderClient;
		this.blackListTokenRepo = blackListTokenRepo;
	}

	public List<UserAccount> listUsers() {
		logger.info("Listing all users");
		return userRepo.findAll();
	}

	public UserAccount getUserById(UUID id) {
		return userRepo.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
	}

	public void deleteUser(UUID id) {
		UserAccount user = getUserById(id);
		userRepo.delete(user);
	}

	public void updatePassword(UUID id, UpdatePasswordRequest request) {
		UserAccount user = getUserById(id);
		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
			throw new InvalidPasswordException("Current password is incorrect");
		}
		user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
		userRepo.save(user);
	}

	public boolean verifyPassword(UUID id, VerifyPasswordRequest request) {
		UserAccount user = getUserById(id);
		try {
			return passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
		} catch (Exception e) {
			logger.error("Password verification error for user {}: {}", id, e.getMessage(), e);
			throw new PasswordVerificationFailedException("Password verification failed", e);
		}
	}

	public List<OrderSummaryResponse> getUserOrders(UUID id, String authorization, String requesterUsername) {
		UserAccount requester = userRepo.findByUsername(requesterUsername)
				.orElseThrow(() -> new UserNotFoundException("User not found: " + requesterUsername));
		if (!requester.getId().equals(id)) {
			throw new IllegalArgumentException("Users can only access their own orders");
		}
		return orderClient.getOrdersByUserId(id, authorization);
	}

	public OrderResponse getOrderDetails(UUID id, Long orderId, String authorization, String requesterUsername) {
		UserAccount requester = userRepo.findByUsername(requesterUsername)
				.orElseThrow(() -> new UserNotFoundException("User not found: " + requesterUsername));
		if (!requester.getId().equals(id)) {
			throw new IllegalArgumentException("Users can only access their own orders");
		}
		OrderResponse order = orderClient.getOrderById(orderId, authorization);
		if (order == null) {
			return null;
		}
		if ((order.getUserId() != id)) {
			throw new IllegalArgumentException("Order does not belong to the user");
		}
		return order;
	}

	public void addTokenToBlackList(BlackListRequest blackListRequest) {
		BlackListToken blackListToken = new BlackListToken();
		blackListRequest.setToken(blackListRequest.getToken());
		blackListTokenRepo.save(blackListToken);
	}

	public boolean getBlacklistTokenIfExist(BlackListRequest blackListRequest) {
		try {
			System.out.println(blackListRequest.getToken());
			BlackListToken blackListToken = blackListTokenRepo.findByToken(blackListRequest.getToken())
					.orElseThrow(() -> new IllegalArgumentException("Token not found: " + blackListRequest.getToken()));
			if (blackListToken != null) {
				return true;
			}
		} catch (Exception e) {
			throw new UserNotFoundException("Request Failed: " + e.getMessage());
		}
		return false;
	}
}
