package com.nur.livequiz.qms.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.Question;
import com.quizSystem.shared.dto.QuizDTO;
import com.quizSystem.shared.dto.QuestionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.stream.Collectors;

public class QuizMapper {

  public static QuizDTO toDTO(Quiz quiz) {
    QuizDTO dto = new QuizDTO();
    dto.setId(quiz.getId());
    dto.setTitle(quiz.getTitle());
    dto.setQuestions(
      quiz.getQuestions().stream()
        .map(QuizMapper::toDTO)
        .collect(Collectors.toList())
    );
    return dto;
  }

  public static QuestionDTO toDTO(Question question) {
    QuestionDTO dto = new QuestionDTO();
    dto.setId(question.getId());
    dto.setQuestionText(question.getQuestionText());

    dto.setOptions(question.getOptions() != null ? question.getOptions() : List.of());
    dto.setCorrectOption(question.getCorrectOption());
    dto.setQuizId(question.getQuiz().getId());
    return dto;
  }

}
