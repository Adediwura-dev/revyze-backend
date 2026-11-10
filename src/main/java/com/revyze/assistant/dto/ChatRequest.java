package com.revyze.assistant.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class ChatRequest {

    private UUID sessionId;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    public ChatRequest() {
    }

    public ChatRequest(UUID sessionId, String message) {
        this.sessionId = sessionId;
        this.message = message;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

