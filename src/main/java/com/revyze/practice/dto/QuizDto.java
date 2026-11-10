package com.revyze.practice.dto;

import com.revyze.practice.entity.Quiz;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class QuizDto {

    private UUID id;
    private String title;
    private String topic;
    private List<QuizQuestionDto> questions;
    private Instant createdAt;

    public QuizDto() {
    }

    public QuizDto(UUID id, String title, String topic, List<QuizQuestionDto> questions, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.topic = topic;
        this.questions = questions;
        this.createdAt = createdAt;
    }

    public static QuizDto fromEntity(Quiz quiz, List<QuizQuestionDto> questions) {
        return new QuizDto(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getTopic(),
                questions,
                quiz.getCreatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<QuizQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionDto> questions) {
        this.questions = questions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
