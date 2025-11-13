package com.quizSystem.RTS.DTO;

import java.util.List;

// Minimal DTO for RTS usage
public class QuizDTO {
  private Long id;
  private String title;
  private String description;
  private String status; // can be string instead of enum for simplicity
  private List<QuestionDTO> questions;

  // Getters & setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  public List<QuestionDTO> getQuestions() { return questions; }
  public void setQuestions(List<QuestionDTO> questions) { this.questions = questions; }
}
