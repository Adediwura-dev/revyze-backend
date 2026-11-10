package com.revyze.practice.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class QuizGenerateRequest {

    @NotBlank(message = "Topic is required")
    private String topic;

    private UUID materialId;

    private int questionCount = 5;

    public QuizGenerateRequest() {
    }

    public QuizGenerateRequest(String topic, UUID materialId, int questionCount) {
        this.topic = topic;
        this.materialId = materialId;
        this.questionCount = questionCount;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public UUID getMaterialId() {
        return materialId;
    }

    public void setMaterialId(UUID materialId) {
        this.materialId = materialId;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }
}
