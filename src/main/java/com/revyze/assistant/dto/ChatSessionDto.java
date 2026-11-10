package com.revyze.assistant.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChatSessionDto {

    private UUID id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ChatMessageDto> messages;

    public ChatSessionDto() {
    }

    public ChatSessionDto(UUID id, String title, Instant createdAt, Instant updatedAt, List<ChatMessageDto> messages) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messages = messages;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<ChatMessageDto> getMessages() {
        return messages;
    }
}

