package jwt.util.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jwt.util.JwtTokenUtil;

public class AuthService {

	public static final String BEARER_PREFIX = "Bearer ";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";
	public static final int BEARER_TOKEN_START_INDEX = 7;
	public static final String LOG_NO_AUTHORIZATION_HEADER = "No authorization header provided";
	public static final String LOG_ACCESS_DENIED_NOT_ADMIN = "Access denied - user is not admin";
	public static final String LOG_ACCESS_DENIED_USER_NO_USER_ROLE = "Access denied - user does not have USER role";
	public static final String LOG_TOKEN_VALIDATION_RESPONSE = "Token validation response: valid={}, username={}, roles={}";
	public static final String LOG_ERROR_VALIDATING_TOKEN = "Error validating token: {}";

	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

	private static DatabaseService databaseService = new DatabaseService();
	
	private AuthService() {}

	public static boolean isAdmin(String bearerToken) {
		try {
			logger.info("Token check - Role is Admin ");
			String token = bearerToken.startsWith(BEARER_PREFIX) ? bearerToken.substring(BEARER_TOKEN_START_INDEX)
					: bearerToken;

			boolean isValidToken = JwtTokenUtil.validateToken(token);

			List<String> roles = null;
			String username = null;
			if (isValidToken) {
				roles = JwtTokenUtil.extractRoles(token);
				username = JwtTokenUtil.extractUsername(token);
				logger.info(LOG_TOKEN_VALIDATION_RESPONSE, isValidToken, username, roles);
			} else {
				logger.info(LOG_TOKEN_VALIDATION_RESPONSE, isValidToken, username, roles);
			}

			return isValidToken && roles.contains(ROLE_ADMIN);

		} catch (Exception e) {
			logger.error(LOG_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
			return false;
		}
	}

	public static boolean isUser(String bearerToken) {
		try {
			logger.info("Token check - Role is User ");

			String token = bearerToken.startsWith(BEARER_PREFIX) ? bearerToken.substring(BEARER_TOKEN_START_INDEX)
					: bearerToken;

			boolean isValidToken = JwtTokenUtil.validateToken(token);

			List<String> roles = null;
			String username = null;
			if (isValidToken) {
				roles = JwtTokenUtil.extractRoles(token);
				username = JwtTokenUtil.extractUsername(token);
				logger.info(LOG_TOKEN_VALIDATION_RESPONSE, isValidToken, username, roles);
			} else {
				logger.info(LOG_TOKEN_VALIDATION_RESPONSE, isValidToken, username, roles);
			}

			return isValidToken && roles.contains(ROLE_USER);

		} catch (Exception e) {
			logger.error(LOG_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
			return false;
		}
	}

	public static UUID getUserId(String bearerToken) {
		try {
			logger.info("Token check - Fetching userId");
			String token = bearerToken.startsWith(BEARER_PREFIX) ? bearerToken.substring(BEARER_TOKEN_START_INDEX)
					: bearerToken;

			boolean isValid = JwtTokenUtil.isTokenValid(token);
			String username = "";
			UUID userID = null;
			if (isValid) {
				username = JwtTokenUtil.extractUsername(token);
				userID = databaseService.fetchUserID(username);
			}

			return isValid ? userID : null;

		} catch (Exception e) {
			logger.error(LOG_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
			return null;
		}
	}

}
