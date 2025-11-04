package com.ecom.userservice.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.userservice.dto.ChatRequest;
import com.ecom.userservice.dto.ChatResponse;
import com.ecom.userservice.dto.FaqDto;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.repository.UserAccountRepository;
import com.ecom.userservice.security.JwtService;
import com.ecom.userservice.service.ChatbotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);
    private final ChatbotService chatbotService;
    private final JwtService jwtService;
    private final UserAccountRepository userRepo;

    public ChatbotController(ChatbotService chatbotService, JwtService jwtService, UserAccountRepository userRepo) {
        this.chatbotService = chatbotService;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<FaqDto>> faqs() {
        return ResponseEntity.ok(chatbotService.getFaqs());
    }

    @PostMapping
    public ResponseEntity<ChatResponse> inquire(@Valid @RequestBody ChatRequest request,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        // If Authorization header present, try to extract username and resolve userId.
        if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
            try {
                String token = authHeader.substring(7).trim();
                if (jwtService.isTokenValid(token)) {
                    String username = jwtService.extractUsername(token);
                    UUID userId = userRepo.findByUsername(username).map(UserAccount::getId).orElse(null);
                    request.setUserId(userId);
                }
            } catch (Exception e) {
                logger.debug("Could not resolve user from Authorization header: {}", e.getMessage());
            }
        }

        logger.info("Chat inquiry from user {}: {}", request.getUserId(), request.getMessage());
        ChatResponse resp = chatbotService.handleInquiry(request);
        return ResponseEntity.ok(resp);
    }
}
