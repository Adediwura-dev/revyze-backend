package com.revyze.practice.dto;

import java.util.List;
import java.util.UUID;

public class QuizSubmitRequest {

    private List<AnswerSubmission> answers;
    private Integer timeSpentSeconds;

    public QuizSubmitRequest() {
    }

    public QuizSubmitRequest(List<AnswerSubmission> answers) {
        this.answers = answers;
    }

    public List<AnswerSubmission> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerSubmission> answers) {
        this.answers = answers;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public static class AnswerSubmission {
        private UUID questionId;
        private Integer questionIndex;
        private String selectedOption; // "A", "B", "C", "D"

        public AnswerSubmission() {
        }

        public AnswerSubmission(int questionIndex, String selectedOption) {
            this.questionIndex = questionIndex;
            this.selectedOption = selectedOption;
        }

        public AnswerSubmission(UUID questionId, Integer questionIndex, String selectedOption) {
            this.questionId = questionId;
            this.questionIndex = questionIndex;
            this.selectedOption = selectedOption;
        }

        public UUID getQuestionId() {
            return questionId;
        }

        public void setQuestionId(UUID questionId) {
            this.questionId = questionId;
        }

        public Integer getQuestionIndex() {
            return questionIndex;
        }

        public void setQuestionIndex(Integer questionIndex) {
            this.questionIndex = questionIndex;
        }

        public String getSelectedOption() {
            return selectedOption;
        }

        public void setSelectedOption(String selectedOption) {
            this.selectedOption = selectedOption;
        }
    }
}

