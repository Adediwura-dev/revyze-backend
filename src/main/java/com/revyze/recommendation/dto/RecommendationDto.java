package com.revyze.recommendation.dto;

public class RecommendationDto {

    private boolean hasRecommendation;
    private String topic;
    private String course;
    private String status;
    private Integer masteryPercentage;
    private Long nextExamInDays;
    private Long lastPracticedDaysAgo;
    private String previousQuizScore;
    private String recommendedSessionTitle;
    private Integer recommendedDurationMinutes;
    private String reason;
    private String title;
    private String description;

    public RecommendationDto() {
    }

    public static RecommendationDto emptyOnboarding() {
        RecommendationDto dto = new RecommendationDto();
        dto.setHasRecommendation(false);
        dto.setTitle("Add a study material to get started");
        dto.setDescription("Once Revyze has something to work with, it can help you identify what to focus on, create practice sessions, and recommend what to study next.");
        return dto;
    }

    public static RecommendationDto populated(
            String topic,
            String course,
            String status,
            int masteryPercentage,
            Long nextExamInDays,
            Long lastPracticedDaysAgo,
            String previousQuizScore,
            String recommendedSessionTitle,
            int recommendedDurationMinutes,
            String reason) {
        RecommendationDto dto = new RecommendationDto();
        dto.setHasRecommendation(true);
        dto.setTopic(topic);
        dto.setCourse(course);
        dto.setStatus(status);
        dto.setMasteryPercentage(masteryPercentage);
        dto.setNextExamInDays(nextExamInDays);
        dto.setLastPracticedDaysAgo(lastPracticedDaysAgo);
        dto.setPreviousQuizScore(previousQuizScore);
        dto.setRecommendedSessionTitle(recommendedSessionTitle);
        dto.setRecommendedDurationMinutes(recommendedDurationMinutes);
        dto.setReason(reason);
        return dto;
    }

    public boolean isHasRecommendation() {
        return hasRecommendation;
    }

    public void setHasRecommendation(boolean hasRecommendation) {
        this.hasRecommendation = hasRecommendation;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMasteryPercentage() {
        return masteryPercentage;
    }

    public void setMasteryPercentage(Integer masteryPercentage) {
        this.masteryPercentage = masteryPercentage;
    }

    public Long getNextExamInDays() {
        return nextExamInDays;
    }

    public void setNextExamInDays(Long nextExamInDays) {
        this.nextExamInDays = nextExamInDays;
    }

    public Long getLastPracticedDaysAgo() {
        return lastPracticedDaysAgo;
    }

    public void setLastPracticedDaysAgo(Long lastPracticedDaysAgo) {
        this.lastPracticedDaysAgo = lastPracticedDaysAgo;
    }

    public String getPreviousQuizScore() {
        return previousQuizScore;
    }

    public void setPreviousQuizScore(String previousQuizScore) {
        this.previousQuizScore = previousQuizScore;
    }

    public String getRecommendedSessionTitle() {
        return recommendedSessionTitle;
    }

    public void setRecommendedSessionTitle(String recommendedSessionTitle) {
        this.recommendedSessionTitle = recommendedSessionTitle;
    }

    public Integer getRecommendedDurationMinutes() {
        return recommendedDurationMinutes;
    }

    public void setRecommendedDurationMinutes(Integer recommendedDurationMinutes) {
        this.recommendedDurationMinutes = recommendedDurationMinutes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
}

