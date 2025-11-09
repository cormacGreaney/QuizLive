package com.nur.livequiz.qms.service;

import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.QuizStatus;
import com.nur.livequiz.qms.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    // Create a new quiz
    public Quiz createQuiz(Quiz quiz) {
        quiz.setStatus(QuizStatus.DRAFT); // default status when created
        return quizRepository.save(quiz);
    }

    // Get all quizzes
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    // Start a quiz (change status to LIVE)
    public Quiz startQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setStatus(QuizStatus.LIVE);
        return quizRepository.save(quiz);
    }

    // End a quiz (change status to ENDED)
    public Quiz endQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setStatus(QuizStatus.ENDED);
        return quizRepository.save(quiz);
    }
}