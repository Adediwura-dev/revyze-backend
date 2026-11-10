package com.revyze.assistant.dto;

import java.util.UUID;

public class ChatResponseDto {

    private UUID sessionId;
    private String sessionTitle;
    private ChatMessageDto userMessage;
    private ChatMessageDto assistantMessage;

    public ChatResponseDto() {
    }

    public ChatResponseDto(UUID sessionId, String sessionTitle, ChatMessageDto userMessage, ChatMessageDto assistantMessage) {
        this.sessionId = sessionId;
        this.sessionTitle = sessionTitle;
        this.userMessage = userMessage;
        this.assistantMessage = assistantMessage;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public ChatMessageDto getUserMessage() {
        return userMessage;
    }

    public ChatMessageDto getAssistantMessage() {
        return assistantMessage;
    }
}

