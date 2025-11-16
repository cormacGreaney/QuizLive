package com.nur.livequiz.qms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nur.livequiz.qms.domain.Question;
import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.QuizStatus;
import com.nur.livequiz.qms.repository.QuizRepository;
import com.quizSystem.shared.dto.QuestionDTO;
import com.quizSystem.shared.dto.QuizDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

  @Autowired
  private QuizRepository quizRepository;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${rts.base.url:http://localhost:8083}")
  private String rtsBaseUrl;

  private final ObjectMapper objectMapper = new ObjectMapper();

  // Create a new quiz
  public Quiz createQuiz(Quiz quiz) {
    quiz.setStatus(QuizStatus.DRAFT);
    return quizRepository.save(quiz);
  }

  // Get all quizzes
  public List<Quiz> getAllQuizzes() {
    return quizRepository.findAll();
  }

  // Start a quiz (change status to LIVE)
  public Quiz startQuiz(Long id) {
    Quiz quiz = quizRepository.findByIdWithQuestions(id)
      .orElseThrow(() -> new RuntimeException("Quiz not found"));
    quiz.setStatus(QuizStatus.LIVE);
    Quiz saved = quizRepository.save(quiz);

    // NOTIFY RTS!
    notifyRTS(saved);

    return saved;
  }

  // End a quiz (change status to ENDED)
  public Quiz endQuiz(Long id) {
    Quiz quiz = quizRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Quiz not found"));
    quiz.setStatus(QuizStatus.ENDED);
    Quiz saved = quizRepository.save(quiz);

    // NOTIFY RTS!
    notifyRTS(saved);

    return saved;
  }

  /**
   * Notify RTS service about quiz changes
   */
  private void notifyRTS(Quiz quiz) {
    try {
      QuizDTO dto = convertToDTO(quiz);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<QuizDTO> request = new HttpEntity<>(dto, headers);

      String url = rtsBaseUrl + "/rts/quiz/update";
      restTemplate.postForObject(url, request, String.class);

      System.out.println("Notified RTS about quiz: " + quiz.getId() + " (status: " + quiz.getStatus() + ")");
    } catch (Exception e) {
      System.err.println("Failed to notify RTS: " + e.getMessage());
      // Don't fail the quiz operation if RTS notification fails
    }
  }

  /**
   * Convert Quiz entity to QuizDTO
   */
  private QuizDTO convertToDTO(Quiz quiz) {
    QuizDTO dto = new QuizDTO();
    dto.setId(quiz.getId());
    dto.setTitle(quiz.getTitle());
    dto.setDescription(quiz.getDescription());
    dto.setStatus(quiz.getStatus().toString());

    // Convert questions
    List<QuestionDTO> questionDTOs = new ArrayList<>();
    if (quiz.getQuestions() != null) {
      for (Question q : quiz.getQuestions()) {
        QuestionDTO qDto = new QuestionDTO();
        qDto.setId(q.getId());
        qDto.setText(q.getQuestionText());
        qDto.setType("MULTIPLE_CHOICE");
        qDto.setCorrectOption(q.getCorrectOption());

        // Parse options JSON string to List
        try {
          List<String> options = objectMapper.readValue(
            q.getOptions(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
          );
          qDto.setOptions(options);
        } catch (Exception e) {
          System.err.println("Failed to parse question options: " + e.getMessage());
          qDto.setOptions(new ArrayList<>());
        }

        questionDTOs.add(qDto);
      }
    }
    dto.setQuestions(questionDTOs);

    return dto;
  }
}
