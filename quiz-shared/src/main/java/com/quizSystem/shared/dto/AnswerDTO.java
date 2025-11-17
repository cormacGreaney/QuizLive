package com.quizSystem.shared.dto;

public class AnswerDTO {
  private Long quizId;
  private Long questionId;
  private String userId;
  private Integer selectedOption;
  private Long timestamp;

  public AnswerDTO() {}

  public AnswerDTO(Long quizId, Long questionId, String userId, Integer selectedOption, Long timestamp) {
    this.quizId = quizId;
    this.questionId = questionId;
    this.userId = userId;
    this.selectedOption = selectedOption;
    this.timestamp = timestamp;
  }

  public Long getQuizId() { return quizId; }
  public void setQuizId(Long quizId) { this.quizId = quizId; }

  public Long getQuestionId() { return questionId; }
  public void setQuestionId(Long questionId) { this.questionId = questionId; }

  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }

  public Integer getSelectedOption() { return selectedOption; }
  public void setSelectedOption(Integer selectedOption) { this.selectedOption = selectedOption; }

  public Long getTimestamp() { return timestamp; }
  public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
