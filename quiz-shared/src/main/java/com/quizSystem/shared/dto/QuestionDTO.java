package com.quizSystem.shared.dto;

import java.util.List;

public class QuestionDTO {
  private Long id;
  private String text;
  private String type;
  private List<String> options;        // ["Option A", "Option B", "Option C", "Option D"]
  private Integer correctOption;       // 0, 1, 2, 3 (index of correct answer)

  // Getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getText() { return text; }
  public void setText(String text) { this.text = text; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public List<String> getOptions() { return options; }
  public void setOptions(List<String> options) { this.options = options; }

  public Integer getCorrectOption() { return correctOption; }
  public void setCorrectOption(Integer correctOption) { this.correctOption = correctOption; }
}
