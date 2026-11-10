package com.revyze.practice.controller;

import com.revyze.common.response.ApiResponse;
import com.revyze.practice.dto.*;
import com.revyze.practice.service.QuizService;
import com.revyze.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/practice")
public class PracticeController {

    private final QuizService service;

    public PracticeController(QuizService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<QuizDto>> createQuiz(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody QuizGenerateRequest request) {
        QuizDto quiz = service.generateQuiz(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Quiz generated", quiz));
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<ApiResponse<QuizDto>> fetchQuiz(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable("id") UUID id) {
        QuizDto quiz = service.getQuiz(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Quiz ready", quiz));
    }

    @PostMapping("/quizzes/{id}/submit")
    public ResponseEntity<ApiResponse<QuizResultDto>> gradeQuiz(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable("id") UUID id,
            @RequestBody QuizSubmitRequest request) {
        QuizResultDto result = service.submitQuiz(user.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok("Results evaluated", result));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<QuizAttemptDto>>> listHistory(
            @AuthenticationPrincipal UserPrincipal user) {
        List<QuizAttemptDto> history = service.getQuizHistory(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("History loaded", history));
    }
}
