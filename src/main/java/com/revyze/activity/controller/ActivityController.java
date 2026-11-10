package com.revyze.activity.controller;

import com.revyze.activity.dto.ActivityDto;
import com.revyze.activity.service.ActivityService;
import com.revyze.common.response.ApiResponse;
import com.revyze.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/activity")
public class ActivityController {

    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<ActivityDto>>> getRecentActivities(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "10") int limit) {
        List<ActivityDto> activities = activityService.getRecentActivities(principal.getId(), limit);
        return ResponseEntity.ok(ApiResponse.ok(activities));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityDto>> logCustomActivity(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, String> body) {
        String type = body.getOrDefault("type", "STUDY_SESSION");
        String title = body.getOrDefault("title", "Study Session Completed");
        String description = body.getOrDefault("description", "Completed a revision block");

        ActivityDto dto = activityService.logActivity(principal.getId(), type, title, description);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}

