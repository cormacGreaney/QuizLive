package com.nur.livequiz.qms.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1024)
    private String questionText;

    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text", length = 512)
    private List<String> options = new ArrayList<>();

    @Column(nullable = false)
    private Integer correctOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonBackReference
    private Quiz quiz;

    public Long getId() { return id; }
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public Integer getCorrectOption() { return correctOption; }
    public Quiz getQuiz() { return quiz; }

    public void setId(Long id) { this.id = id; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setOptions(List<String> options) { this.options = options; }
    public void setCorrectOption(Integer correctOption) { this.correctOption = correctOption; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}
