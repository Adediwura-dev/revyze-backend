package com.revyze.recommendation.controller;

import com.revyze.common.response.ApiResponse;
import com.revyze.recommendation.dto.RecommendationDto;
import com.revyze.recommendation.service.RecommendationEngineService;
import com.revyze.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendationEngineService engineService;

    public RecommendationController(RecommendationEngineService engineService) {
        this.engineService = engineService;
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<RecommendationDto>> fetchCurrent(
            @AuthenticationPrincipal UserPrincipal user) {
        RecommendationDto recommendation = engineService.getRecommendationForUser(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Recommendation ready", recommendation));
    }
}
