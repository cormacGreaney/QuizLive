package com.quizSystem.shared.dto;

public class AnswerResultDTO {
  private String userId;
  private Long questionId;
  private boolean correct;
  private Integer correctOption;
  private Integer newScore;

  public AnswerResultDTO() {}

  public AnswerResultDTO(String userId, Long questionId, boolean correct, Integer correctOption, Integer newScore) {
    this.userId = userId;
    this.questionId = questionId;
    this.correct = correct;
    this.correctOption = correctOption;
    this.newScore = newScore;
  }

  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }

  public Long getQuestionId() { return questionId; }
  public void setQuestionId(Long questionId) { this.questionId = questionId; }

  public boolean isCorrect() { return correct; }
  public void setCorrect(boolean correct) { this.correct = correct; }

  public Integer getCorrectOption() { return correctOption; }
  public void setCorrectOption(Integer correctOption) { this.correctOption = correctOption; }

  public Integer getNewScore() { return newScore; }
  public void setNewScore(Integer newScore) { this.newScore = newScore; }
}
