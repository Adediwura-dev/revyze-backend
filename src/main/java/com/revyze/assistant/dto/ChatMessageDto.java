package com.revyze.assistant.dto;

import java.time.Instant;
import java.util.UUID;

public class ChatMessageDto {

    private UUID id;
    private UUID sessionId;
    private String sender;
    private String content;
    private Instant createdAt;

    public ChatMessageDto() {
    }

    public ChatMessageDto(UUID id, UUID sessionId, String sender, String content, Instant createdAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.sender = sender;
        this.content = content;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

