package com.revyze.practice.dto;

import java.util.List;

public class QuestionResultDto {

    private int questionIndex;
    private String questionText;
    private List<String> options;
    private String selectedOption;
    private String correctOption;
    private boolean correct;
    private String explanation;

    public QuestionResultDto() {
    }

    public QuestionResultDto(int questionIndex, String questionText, List<String> options,
                             String selectedOption, String correctOption, boolean correct, String explanation) {
        this.questionIndex = questionIndex;
        this.questionText = questionText;
        this.options = options;
        this.selectedOption = selectedOption;
        this.correctOption = correctOption;
        this.correct = correct;
        this.explanation = explanation;
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

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
