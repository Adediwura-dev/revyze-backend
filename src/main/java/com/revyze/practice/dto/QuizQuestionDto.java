package com.revyze.practice.dto;

import com.revyze.practice.entity.QuizQuestion;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class QuizQuestionDto {

    private UUID id;
    private int questionIndex;
    private String questionText;
    private List<String> options;

    public QuizQuestionDto() {
    }

    public QuizQuestionDto(UUID id, int questionIndex, String questionText, List<String> options) {
        this.id = id;
        this.questionIndex = questionIndex;
        this.questionText = questionText;
        this.options = options;
    }

    public static QuizQuestionDto fromEntity(QuizQuestion entity) {
        List<String> opts = Arrays.asList(
                entity.getOptionA(),
                entity.getOptionB(),
                entity.getOptionC(),
                entity.getOptionD()
        );
        return new QuizQuestionDto(
                entity.getId(),
                entity.getQuestionIndex(),
                entity.getQuestionText(),
                opts
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
