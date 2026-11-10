package com.revyze.assistant.controller;

import com.revyze.assistant.dto.ChatRequest;
import com.revyze.assistant.dto.ChatResponseDto;
import com.revyze.assistant.dto.ChatSessionDto;
import com.revyze.assistant.service.AssistantService;
import com.revyze.common.response.ApiResponse;
import com.revyze.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatResponseDto>> chat(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChatRequest request) {
        ChatResponseDto response = assistantService.sendMessage(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Message processed successfully", response));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<ChatSessionDto>>> listSessions(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ChatSessionDto> sessions = assistantService.getSessions(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(sessions));
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<ChatSessionDto>> getSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        ChatSessionDto session = assistantService.getSessionWithMessages(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(session));
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        assistantService.deleteSession(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Session deleted successfully", null));
    }
}
