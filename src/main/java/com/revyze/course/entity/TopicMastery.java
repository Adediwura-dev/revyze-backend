package com.revyze.course.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "topic_mastery", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "topicId"})
})
public class TopicMastery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID topicId;

    @Column(nullable = false)
    private int masteryPercentage;

    private Instant lastPracticedAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public TopicMastery() {
    }

    public TopicMastery(UUID userId, UUID topicId, int masteryPercentage) {
        this.userId = userId;
        this.topicId = topicId;
        this.masteryPercentage = masteryPercentage;
        this.updatedAt = Instant.now();
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getTopicId() {
        return topicId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public int getMasteryPercentage() {
        return masteryPercentage;
    }

    public void setMasteryPercentage(int masteryPercentage) {
        this.masteryPercentage = masteryPercentage;
    }

    public Instant getLastPracticedAt() {
        return lastPracticedAt;
    }

    public void setLastPracticedAt(Instant lastPracticedAt) {
        this.lastPracticedAt = lastPracticedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
