package com.nur.livequiz.qms.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String questionText;

    private Integer correctOption;

    // Many questions belong to one quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    // Getters & Setters
    public Long getId() { return id; }
    public String getQuestionText() { return questionText; }
    public Integer getCorrectOption() { return correctOption; }
    public Quiz getQuiz() { return quiz; }

    public void setId(Long id) { this.id = id; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setCorrectOption(Integer correctOption) { this.correctOption = correctOption; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}