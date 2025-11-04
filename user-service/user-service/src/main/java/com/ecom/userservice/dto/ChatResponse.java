package com.ecom.userservice.dto;

public class ChatResponse {
    private String answer;
    private boolean faqMatched;

    public ChatResponse() {}

    public ChatResponse(String answer, boolean faqMatched) {
        this.answer = answer;
        this.faqMatched = faqMatched;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public boolean isFaqMatched() { return faqMatched; }
    public void setFaqMatched(boolean faqMatched) { this.faqMatched = faqMatched; }
}
