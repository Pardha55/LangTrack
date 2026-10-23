
package com.pardha.langtrack.dto;

public class AnswerFeedback {

    private Long questionId;
    private String selectedAnswer;
    private String correctAnswer;
    private boolean correct;

    public AnswerFeedback() {
    }

    public AnswerFeedback(
            Long questionId,
            String selectedAnswer,
            String correctAnswer,
            boolean correct) {

        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.correct = correct;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }
}
