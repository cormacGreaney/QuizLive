package com.nur.livequiz.qms.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1024)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizStatus status = QuizStatus.DRAFT;

    @Column(nullable = false)
    private Long createdBy; // owner user id

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Question> questions = new ArrayList<>();

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public QuizStatus getStatus() { return status; }
    public Long getCreatedBy() { return createdBy; }
    public List<Question> getQuestions() { return questions; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(QuizStatus status) { this.status = status; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}
