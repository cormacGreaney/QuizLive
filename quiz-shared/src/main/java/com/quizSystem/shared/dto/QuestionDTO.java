package com.quizSystem.shared.dto;

public class QuestionDTO {
  private Long id;
  private String text;
  private String type;

  // Getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getText() { return text; }
  public void setText(String text) { this.text = text; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
}
