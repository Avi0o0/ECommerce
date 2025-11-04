package com.ecom.userservice.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.userservice.dto.ChatRequest;
import com.ecom.userservice.dto.ChatResponse;
import com.ecom.userservice.dto.FaqDto;
import com.ecom.userservice.entity.ChatInquiry;
import com.ecom.userservice.repository.ChatInquiryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);

    private final ChatInquiryRepository inquiryRepository;
    private final ObjectMapper objectMapper;
    private final List<FaqDto> faqs = new ArrayList<>();

    public ChatbotService(ChatInquiryRepository inquiryRepository, ObjectMapper objectMapper) {
        this.inquiryRepository = inquiryRepository;
        this.objectMapper = objectMapper;
        loadFaqs();
    }

    private void loadFaqs() {
        try {
            ClassPathResource res = new ClassPathResource("chatbot_faqs.json");
            byte[] data = res.getInputStream().readAllBytes();
            String json = new String(data, StandardCharsets.UTF_8);
            List<FaqDto> loaded = objectMapper.readValue(json, new TypeReference<List<FaqDto>>() {});
            if (loaded != null) faqs.addAll(loaded);
            logger.info("Loaded {} chatbot faqs", faqs.size());
        } catch (IOException e) {
            logger.warn("Could not load chatbot faqs: {}", e.getMessage());
        }
    }

    public List<FaqDto> getFaqs() {
        return List.copyOf(faqs);
    }

    @Transactional
    public ChatResponse handleInquiry(ChatRequest request) {
        String message = request.getMessage() == null ? "" : request.getMessage().trim();
        String answer = "Sorry, I don't have an answer for that right now. Our support team will contact you.";
        boolean matched = false;

        String msgLower = message.toLowerCase();
        for (FaqDto f : faqs) {
            if (matchesFaq(msgLower, f)) {
                answer = f.getAnswer();
                matched = true;
                break;
            }
        }

        // Persist inquiry only when a logged-in user (userId present) made the request
        if (request.getUserId() != null) {
            ChatInquiry saved = new ChatInquiry(request.getUserId(), message, answer);
            inquiryRepository.save(saved);
        }

        return new ChatResponse(answer, matched);
    }

    private boolean matchesFaq(String msgLower, FaqDto f) {
        if (msgLower.isBlank()) return false;
        String question = safeLower(f.getQuestion());

        if (isDirectMatch(msgLower, question)) return true;
        if (aliasMatches(msgLower, f)) return true;
        return fallbackMatch(msgLower, question);
    }

    private String safeLower(String que) {
        return que == null ? "" : que.toLowerCase();
    }

    private boolean isDirectMatch(String msgLower, String question) {
        if (question.isBlank()) return false;
        return question.contains(msgLower) || msgLower.contains(question);
    }

    private boolean aliasMatches(String msgLower, FaqDto f) {
        List<String> aliases = f.getAliases();
        if (aliases == null) return false;

        return aliases.stream()
                .filter(Objects::nonNull)
                .map(a -> a.toLowerCase().trim())
                .filter(a -> !a.isEmpty())
                .anyMatch(a -> msgLower.contains(a) || a.contains(msgLower) || containsAllTokens(msgLower, a));
    }

    private boolean fallbackMatch(String msgLower, String question) {
        if (question.isBlank()) return false;
        return containsAnyKeyword(msgLower, question);
    }

    private boolean containsAllTokens(String message, String phrase) {
        if (message.isBlank() || phrase.isBlank()) return false;
        String[] tokens = phrase.split("\\s+");
        int matched = 0;
        for (String t : tokens) {
            if (t.length() <= 2) continue;
            if (message.contains(t)) matched++;
        }
        return matched >= Math.max(1, tokens.length / 2); // require at least half the tokens to match
    }
    private boolean containsAnyKeyword(String message, String question) {
        if (message.isBlank() || question.isBlank()) return false;
        String[] parts = question.split("\\s+");
        for (String p : parts) {
            if (p.length() <= 3) continue; // skip short words
            if (message.contains(p)) return true;
        }
        return false;
    }
}
