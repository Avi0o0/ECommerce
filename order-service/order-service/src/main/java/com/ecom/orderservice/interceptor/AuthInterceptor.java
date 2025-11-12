package com.ecom.orderservice.interceptor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ecom.orderservice.constants.OrderServiceConstants;
import com.ecom.orderservice.dto.GlobalErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jwt.util.JwtTokenUtil;
import jwt.util.service.AuthService;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

	@Override
	public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull Object handler) throws Exception {

		logger.info("PreHandle: Auth Check at {}", request.getRequestURI());
		
		try {
			String authHeader = request.getHeader("Authorization");
			String token = "";
			if (StringUtils.hasText(authHeader)) {
				token = authHeader.startsWith(OrderServiceConstants.BEARER_PREFIX)
						? authHeader.substring(OrderServiceConstants.BEARER_TOKEN_START_INDEX)
						: authHeader;
			}
			boolean isValidToken = JwtTokenUtil.isTokenValid(token);
			logger.info("is Token Valid {}", isValidToken);
			if (isValidToken) {
				boolean isUser = AuthService.isUser(token);
				boolean isAdmin = AuthService.isAdmin(token);
				String userId = AuthService.getUserId(token).toString();
				request.setAttribute("isValidToken", isValidToken);
				request.setAttribute("isUser", isUser);
				request.setAttribute("isAdmin", isAdmin);
				request.setAttribute("userId", userId);
				return true;
			} else {
				sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
			}
		} catch (ExpiredJwtException e) {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token has expired. Please log in again.");
			return false;
		} catch (JwtException e) {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
					"Invalid token. Please provide a valid token.");
			return false;
		} catch (Exception e) {
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An unexpected error occurred during authentication.");
			return false;
		}
		return false;
	}

	@Override
	public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			modelAndView.addObject("footerMessage", "© 2025 Akshat's Ecom App");
		}
		logger.info("postHandle: Controller executed successfully for URI: {}", request.getRequestURI());

	}

	@Override
	public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull Object handler, @Nullable Exception ex) throws Exception {
		if (ex != null) {
	        logger.error("afterCompletion: Exception occurred in request: {}", request.getRequestURI(), ex);
	    } else {
	        logger.info("afterCompletion: Request completed successfully for {}", request.getRequestURI());
	    }
	}

	private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		GlobalErrorResponse responseBody = new GlobalErrorResponse(status, message, "Token Validation Failed!!");
		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = mapper.writeValueAsString(responseBody);

		response.getWriter().write(jsonResponse);
	}

}
