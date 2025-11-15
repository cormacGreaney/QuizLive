package com.nur.livequiz.qms.mapper;

import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.Question;
import com.quizSystem.shared.dto.QuizDTO;
import com.quizSystem.shared.dto.QuestionDTO;

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
    // parse JSON string options if needed
    dto.setOptions(question.getOptions() != null ?
      List.of(question.getOptions().split(",")) : List.of()
    );
    dto.setCorrectOption(question.getCorrectOption());
    return dto;
  }
}
