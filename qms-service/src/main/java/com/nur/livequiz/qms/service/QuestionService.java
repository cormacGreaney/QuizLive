package com.nur.livequiz.qms.service;

import com.nur.livequiz.qms.domain.Question;
import com.nur.livequiz.qms.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Question addQuestion(Question question) {
        return questionRepository.save(question);
    }

    public List<Question> getQuestionsByQuiz(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }
}