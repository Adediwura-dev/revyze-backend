package com.revyze.practice.dto;

import com.revyze.practice.entity.QuizAttempt;
import java.time.Instant;
import java.util.UUID;

public class QuizAttemptDto {

    private UUID id;
    private UUID quizId;
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private int percentage;
    private Instant completedAt;

    public QuizAttemptDto() {
    }

    public QuizAttemptDto(UUID id, UUID quizId, String quizTitle, int score, int totalQuestions, int percentage, Instant completedAt) {
        this.id = id;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.completedAt = completedAt;
    }

    public static QuizAttemptDto fromEntity(QuizAttempt attempt, String quizTitle) {
        return new QuizAttemptDto(
                attempt.getId(),
                attempt.getQuizId(),
                quizTitle,
                attempt.getScore(),
                attempt.getTotalQuestions(),
                attempt.getPercentage(),
                attempt.getCompletedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
