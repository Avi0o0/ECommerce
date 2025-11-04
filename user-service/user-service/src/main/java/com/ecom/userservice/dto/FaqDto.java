package com.ecom.userservice.dto;

import java.util.List;

public class FaqDto {
    private String question;
    private String answer;
    // optional aliases/variations to improve matching (e.g. "contact us", "contact info", "how can i contact")
    private List<String> aliases;

    public FaqDto() {}

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<String> getAliases() { return aliases; }
    public void setAliases(List<String> aliases) { this.aliases = aliases; }
}
