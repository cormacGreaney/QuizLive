package com.nur.livequiz.qms.controller;

import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    // Create new quiz
    @PostMapping
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizService.createQuiz(quiz);
    }

    // Get all quizzes
    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    // Start a quiz
    @PostMapping("/{id}/start")
    public Quiz startQuiz(@PathVariable Long id) {
        return quizService.startQuiz(id);
    }

    // End a quiz
    @PostMapping("/{id}/end")
    public Quiz endQuiz(@PathVariable Long id) {
        return quizService.endQuiz(id);
    }
}