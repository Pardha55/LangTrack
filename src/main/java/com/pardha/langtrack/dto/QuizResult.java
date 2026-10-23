
package com.pardha.langtrack.dto;

public class QuizResult {

    private int score;
    private int totalQuestions;
    private double percentage;
    private boolean passed;
    private String message;
    private int xpEarned;
    
    public QuizResult(int score,
                  int totalQuestions,
                  double percentage,
                  boolean passed,
                  String message,
                  int xpEarned) {
    this.score = score;
    this.totalQuestions = totalQuestions;
    this.percentage = percentage;
    this.passed = passed;
    this.message = message;
    this.xpEarned = xpEarned;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public QuizResult(int score,
                      int totalQuestions,
                      double percentage,
                      boolean passed,
                      String message) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.passed = passed;
        this.message = message;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public double getPercentage() {
        return percentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getMessage() {
        return message;
    }
}
