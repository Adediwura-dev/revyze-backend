package com.revyze.activity.dto;

import com.revyze.activity.entity.RecentActivity;
import java.time.Instant;
import java.util.UUID;

public class ActivityDto {

    private UUID id;
    private String activityType;
    private String title;
    private String description;
    private Instant createdAt;

    public ActivityDto() {
    }

    public ActivityDto(UUID id, String activityType, String title, String description, Instant createdAt) {
        this.id = id;
        this.activityType = activityType;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static ActivityDto fromEntity(RecentActivity entity) {
        return new ActivityDto(
                entity.getId(),
                entity.getActivityType(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
