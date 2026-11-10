package com.revyze.activity.service;

import com.revyze.activity.dto.ActivityDto;
import com.revyze.activity.entity.RecentActivity;
import com.revyze.activity.repository.RecentActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final RecentActivityRepository activityRepository;

    @Autowired
    public ActivityService(RecentActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    public ActivityDto logActivity(UUID userId, String type, String title, String description) {
        RecentActivity activity = new RecentActivity(userId, type, title, description);
        RecentActivity saved = activityRepository.save(activity);
        return ActivityDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getRecentActivities(UUID userId, int limit) {
        return activityRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit))
                .stream()
                .map(ActivityDto::fromEntity)
                .collect(Collectors.toList());
    }
}
