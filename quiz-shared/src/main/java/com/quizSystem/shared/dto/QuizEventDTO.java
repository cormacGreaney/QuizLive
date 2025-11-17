package com.quizSystem.shared.dto;

public class QuizEventDTO {
  private Long quizId;
  private String eventType;
  private QuestionDTO currentQuestion;
  private Integer questionNumber;
  private Integer totalQuestions;

  public QuizEventDTO() {}

  public QuizEventDTO(Long quizId, String eventType, QuestionDTO currentQuestion, Integer questionNumber, Integer totalQuestions) {
    this.quizId = quizId;
    this.eventType = eventType;
    this.currentQuestion = currentQuestion;
    this.questionNumber = questionNumber;
    this.totalQuestions = totalQuestions;
  }

  public Long getQuizId() { return quizId; }
  public void setQuizId(Long quizId) { this.quizId = quizId; }

  public String getEventType() { return eventType; }
  public void setEventType(String eventType) { this.eventType = eventType; }

  public QuestionDTO getCurrentQuestion() { return currentQuestion; }
  public void setCurrentQuestion(QuestionDTO currentQuestion) { this.currentQuestion = currentQuestion; }

  public Integer getQuestionNumber() { return questionNumber; }
  public void setQuestionNumber(Integer questionNumber) { this.questionNumber = questionNumber; }

  public Integer getTotalQuestions() { return totalQuestions; }
  public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
}
