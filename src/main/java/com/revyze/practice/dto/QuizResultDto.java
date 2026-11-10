package com.revyze.practice.dto;

import java.util.List;
import java.util.UUID;

public class QuizResultDto {

    private UUID attemptId;
    private UUID quizId;
    private int score;
    private int totalQuestions;
    private int percentage;
    private String feedback;
    private List<QuestionResultDto> results;

    public QuizResultDto() {
    }

    public QuizResultDto(UUID attemptId, UUID quizId, int score, int totalQuestions,
                         int percentage, String feedback, List<QuestionResultDto> results) {
        this.attemptId = attemptId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.feedback = feedback;
        this.results = results;
    }

    public UUID getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(UUID attemptId) {
        this.attemptId = attemptId;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
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

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public List<QuestionResultDto> getResults() {
        return results;
    }

    public void setResults(List<QuestionResultDto> results) {
        this.results = results;
    }
}
