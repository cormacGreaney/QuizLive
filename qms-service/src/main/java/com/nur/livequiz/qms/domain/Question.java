package com.nur.livequiz.qms.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String questionText;

    @Column(columnDefinition = "JSON")
    private String options; // JSON array of option strings

    private Integer correctOption;

    // Many questions belong to one quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    @JsonBackReference
    private Quiz quiz;

    // Getters & Setters
    public Long getId() { return id; }
    public String getQuestionText() { return questionText; }
    public String getOptions() { return options; }
    public Integer getCorrectOption() { return correctOption; }
    public Quiz getQuiz() { return quiz; }

    public void setId(Long id) { this.id = id; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setOptions(String options) { this.options = options; }
    public void setCorrectOption(Integer correctOption) { this.correctOption = correctOption; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}