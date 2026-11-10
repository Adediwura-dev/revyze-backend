package com.revyze.practice.dto;

import java.util.List;

public class QuizSubmitRequest {

    private List<AnswerSubmission> answers;

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

    public static class AnswerSubmission {
        private int questionIndex;
        private String selectedOption; // "A", "B", "C", "D"

        public AnswerSubmission() {
        }

        public AnswerSubmission(int questionIndex, String selectedOption) {
            this.questionIndex = questionIndex;
            this.selectedOption = selectedOption;
        }

        public int getQuestionIndex() {
            return questionIndex;
        }

        public void setQuestionIndex(int questionIndex) {
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

