package com.revyze.progress.dto;

import java.time.Instant;
import java.util.UUID;

public class TopicProgressDto {

    private UUID topicId;
    private String topicName;
    private String courseName;
    private int masteryPercentage;
    private int quizzesTaken;
    private Instant lastReviewedAt;
    private String status; // "Mastered" (>=80), "In Progress" (50-79), "Needs Attention" (<50)

    public TopicProgressDto() {
    }

    public TopicProgressDto(UUID topicId, String topicName, String courseName, int masteryPercentage,
                            int quizzesTaken, Instant lastReviewedAt, String status) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.courseName = courseName;
        this.masteryPercentage = masteryPercentage;
        this.quizzesTaken = quizzesTaken;
        this.lastReviewedAt = lastReviewedAt;
        this.status = status;
    }

    public UUID getTopicId() {
        return topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getMasteryPercentage() {
        return masteryPercentage;
    }

    public int getQuizzesTaken() {
        return quizzesTaken;
    }

    public Instant getLastReviewedAt() {
        return lastReviewedAt;
    }

    public String getStatus() {
        return status;
    }
}

