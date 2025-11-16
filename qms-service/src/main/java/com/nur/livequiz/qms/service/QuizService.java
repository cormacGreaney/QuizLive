package com.nur.livequiz.qms.service;

import com.nur.livequiz.qms.client.RTSClient;
import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.QuizStatus;
import com.nur.livequiz.qms.repository.QuizRepository;
import com.quizSystem.shared.dto.QuizDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nur.livequiz.qms.mapper.QuizMapper;


import java.util.List;

import static com.nur.livequiz.qms.mapper.QuizMapper.toDTO;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private RTSClient rtsClient;

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
      quizRepository.save(quiz);
        QuizDTO dto = toDTO(quiz);
         rtsClient.quizStarted(dto);
         return quiz;

    }

    // End a quiz (change status to ENDED)
    public Quiz endQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setStatus(QuizStatus.ENDED);
        quizRepository.save(quiz);
        QuizDTO dto = toDTO(quiz);
        rtsClient.quizUpdated(dto);
        return quiz;

    }
}
