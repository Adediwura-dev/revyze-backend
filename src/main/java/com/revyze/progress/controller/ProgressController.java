package com.revyze.progress.controller;

import com.revyze.common.response.ApiResponse;
import com.revyze.progress.dto.ProgressSummaryDto;
import com.revyze.progress.dto.TopicProgressDto;
import com.revyze.progress.service.ProgressService;
import com.revyze.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ProgressSummaryDto>> getSummary(
            @AuthenticationPrincipal UserPrincipal principal) {
        ProgressSummaryDto summary = progressService.getProgressSummary(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<List<TopicProgressDto>>> getTopicProgress(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<TopicProgressDto> topics = progressService.getTopicProgress(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(topics));
    }
}
