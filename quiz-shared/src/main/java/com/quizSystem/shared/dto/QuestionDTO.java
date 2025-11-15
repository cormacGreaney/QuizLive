package com.quizSystem.shared.dto;

import java.util.List;

public class QuestionDTO {
  private Long id;
  private String questionText;
  private List<String> options; // not JSON string
  private Integer correctOption; // optional to hide later
  private Long quizId;

  public Long getQuizId() { return quizId; }
  public Long getId() { return id; }
  public String getQuestionText() { return questionText; }
  public List<String> getOptions() { return options; }
  public Integer getCorrectOption() { return correctOption; }

  public void setQuizId(Long quizId) { this.quizId = quizId; }
  public void setId(Long id) { this.id = id; }
  public void setQuestionText(String questionText) { this.questionText = questionText; }
  public void setOptions(List<String> options) { this.options = options; }
  public void setCorrectOption(Integer correctOption) { this.correctOption = correctOption; }
}
