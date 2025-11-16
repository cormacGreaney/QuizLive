package com.quizSystem.RTS.ws;

import com.quizSystem.RTS.service.RedisTestService;
import com.quizSystem.shared.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Controller
public class RtsWsController {

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @Autowired
  private RedisTestService redisService;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  // ============================================
  // HEARTBEAT (Keep this for testing)
  // ============================================

  @Scheduled(fixedRate = 1000)
  public void sendHeartbeat() {
    String heartbeat = "Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): "
      + LocalDateTime.now(ZoneOffset.UTC).format(formatter);
    messagingTemplate.convertAndSend("/topic/heartbeat", heartbeat);
  }

  // ============================================
  // PARTICIPANT ANSWER SUBMISSION
  // ============================================

  /**
   * Participants submit answers here
   * Endpoint: /app/quiz/{quizId}/answer
   */
  @MessageMapping("/quiz/{quizId}/answer")
  public void handleAnswer(@DestinationVariable Long quizId,
                           @Payload AnswerDTO answer,
                           SimpMessageHeaderAccessor headerAccessor) {
    // Ensure Redis keys use the correct quiz scope
    if (answer.getQuizId() == null) {
      answer.setQuizId(quizId);
    }

    System.out.println(" Received answer from user " + answer.getUserId()
      + " for question " + answer.getQuestionId()
      + " - selected option: " + answer.getSelectedOption());

    // Check if user already answered this question
    if (redisService.hasUserAnswered(quizId, answer.getQuestionId(), answer.getUserId())) {
      System.out.println(" User " + answer.getUserId() + " already answered this question - ignoring duplicate");
      return;
    }

    // Get the quiz to find the correct answer
    QuizDTO quiz = redisService.getQuiz(quizId);
    if (quiz == null) {
      System.err.println(" Quiz not found in Redis: " + quizId);
      return;
    }

    // Find the question
    QuestionDTO question = quiz.getQuestions().stream()
      .filter(q -> q.getId().equals(answer.getQuestionId()))
      .findFirst()
      .orElse(null);

    if (question == null) {
      System.err.println(" Question not found: " + answer.getQuestionId());
      return;
    }

    // Check if answer is correct
    boolean isCorrect = question.getCorrectOption().equals(answer.getSelectedOption());

    // Save answer to Redis
    redisService.saveAnswer(answer);

    // Update score if correct (10 points per correct answer)
    int newScore = redisService.getUserScore(quizId, answer.getUserId());
    if (isCorrect) {
      redisService.incrementUserScore(quizId, answer.getUserId(), 10);
      newScore += 10;
      System.out.println("CORRECT! User " + answer.getUserId() + " earned 10 points. New score: " + newScore);
    } else {
      System.out.println(" WRONG! User " + answer.getUserId() + " selected option " + answer.getSelectedOption()
        + " but correct was " + question.getCorrectOption());
    }

    // Send result back to THIS user only
    AnswerResultDTO result = new AnswerResultDTO(
      answer.getUserId(),
      answer.getQuestionId(),
      isCorrect,
      question.getCorrectOption(),
      newScore
    );

    messagingTemplate.convertAndSend(
      "/topic/quiz/" + quizId + "/user/" + answer.getUserId() + "/result",
      result
    );

    // Broadcast updated leaderboard to ALL participants
    LeaderboardDTO leaderboard = redisService.getLeaderboard(quizId);
    messagingTemplate.convertAndSend(
      "/topic/quiz/" + quizId + "/leaderboard",
      leaderboard
    );

    System.out.println(" Leaderboard updated and broadcast to all participants");
  }

  // ============================================
  // PARTICIPANT CONNECTION MANAGEMENT
  // ============================================

  /**
   * Participant joins a quiz
   * Endpoint: /app/quiz/{quizId}/join
   */
  @MessageMapping("/quiz/{quizId}/join")
  public void handleJoin(@DestinationVariable Long quizId,
                         @Payload String userId) {

    System.out.println(" User " + userId + " joined quiz " + quizId);

    redisService.addParticipant(quizId, userId);

    // Send current participant count to everyone
    Long participantCount = redisService.getParticipantCount(quizId);
    messagingTemplate.convertAndSend(
      "/topic/quiz/" + quizId + "/participants",
      participantCount
    );

    // Send current leaderboard to the new user
    LeaderboardDTO leaderboard = redisService.getLeaderboard(quizId);
    messagingTemplate.convertAndSend(
      "/topic/quiz/" + quizId + "/leaderboard",
      leaderboard
    );

    System.out.println(" User " + userId + " successfully joined. Total participants: " + participantCount);
  }

  /**
   * Participant leaves a quiz
   * Endpoint: /app/quiz/{quizId}/leave
   */
  @MessageMapping("/quiz/{quizId}/leave")
  public void handleLeave(@DestinationVariable Long quizId,
                          @Payload String userId) {

    System.out.println("User " + userId + " left quiz " + quizId);

    redisService.removeParticipant(quizId, userId);

    // Send updated participant count to everyone
    Long participantCount = redisService.getParticipantCount(quizId);
    messagingTemplate.convertAndSend(
      "/topic/quiz/" + quizId + "/participants",
      participantCount
    );
  }

  // ============================================
  // ADMIN CONTROLS
  // ============================================

  /**
   * Admin advances to next question
   * Endpoint: /app/quiz/{quizId}/admin/next-question
   */
  @MessageMapping("/quiz/{quizId}/admin/next-question")
  public void handleNextQuestion(@DestinationVariable Long quizId,
                                 @Payload QuizEventDTO event) {

    System.out.println("Admin advancing to question " + event.getQuestionNumber() + " for quiz " + quizId);

    // Update current question in Redis
    if (event.getCurrentQuestion() != null) {
      redisService.setCurrentQuestion(quizId, event.getCurrentQuestion().getId());
    }

    // Broadcast question change to ALL participants
    messagingTemplate.convertAndSend(
      "/topic/quiz/" + quizId + "/event",
      event
    );

    System.out.println("Question " + event.getQuestionNumber() + " broadcast to all participants");
  }

  /**
   * Admin ends quiz
   * Endpoint: /app/quiz/{quizId}/admin/end
   */
  @MessageMapping("/quiz/{quizId}/admin/end")
  public void handleEndQuiz(@DestinationVariable Long quizId) {

    System.out.println(" Admin ending quiz " + quizId);

    // Get final leaderboard
    LeaderboardDTO finalLeaderboard = redisService.getLeaderboard(quizId);

    // Broadcast quiz ended event with final results
    QuizEventDTO endEvent = new QuizEventDTO();
    endEvent.setQuizId(quizId);
    endEvent.setEventType("QUIZ_ENDED");

    messagingTemplate.convertAndSend(
      "/topic/quiz/" + quizId + "/event",
      endEvent
    );

    messagingTemplate.convertAndSend(
      "/topic/quiz/" + quizId + "/final-results",
      finalLeaderboard
    );

    System.out.println("Quiz " + quizId + " ended. Final leaderboard sent with " + finalLeaderboard.getTotalParticipants() + " participants.");
  }
}
