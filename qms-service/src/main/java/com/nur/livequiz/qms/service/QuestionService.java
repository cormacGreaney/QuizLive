package com.nur.livequiz.qms.service;

import com.nur.livequiz.qms.client.RTSClient;
import com.nur.livequiz.qms.domain.Question;
import com.nur.livequiz.qms.repository.QuestionRepository;
import com.quizSystem.shared.dto.QuestionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nur.livequiz.qms.mapper.QuizMapper.toDTO;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
   @Autowired private RTSClient rtsClient;
    public Question addQuestion(Question question) {
      questionRepository.save(question);
      QuestionDTO dto = toDTO(question);
      rtsClient.questionAdded(dto);
        return question;
    }

    public List<Question> getQuestionsByQuiz(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }
}
