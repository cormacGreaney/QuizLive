package com.nur.livequiz.qms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nur.livequiz.qms.domain.Question;
import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.QuizStatus;
import com.nur.livequiz.qms.repository.QuizRepository;
import com.quizSystem.shared.dto.QuestionDTO;
import com.nur.livequiz.qms.client.RTSClient;
import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.QuizStatus;
import com.nur.livequiz.qms.repository.QuizRepository;
import com.quizSystem.shared.dto.QuizDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.nur.livequiz.qms.mapper.QuizMapper;


import java.util.ArrayList;
import java.util.List;

import static com.nur.livequiz.qms.mapper.QuizMapper.toDTO;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private RTSClient rtsClient;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${rts.base.url:http://localhost:8083}")
  private String rtsBaseUrl;

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
