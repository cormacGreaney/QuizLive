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

  // HEARTBEAT
  @Scheduled(fixedRate = 1000)
  public void sendHeartbeat() {
    String heartbeat = "Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): "
        + LocalDateTime.now(ZoneOffset.UTC).format(formatter);
    messagingTemplate.convertAndSend("/topic/heartbeat", heartbeat);
  }

  // PARTICIPANT ANSWER
  @MessageMapping("/quiz/{quizId}/answer")
  public void handleAnswer(@DestinationVariable Long quizId,
                           @Payload AnswerDTO answer,
                           SimpMessageHeaderAccessor headerAccessor) {

    if (answer.getQuizId() == null) {
      answer.setQuizId(quizId);
    }

    // Ensure quiz is cached in Redis (pulls via gateway if missing)
    redisService.ensureQuizCached(quizId);

    System.out.println(" Received answer from user " + answer.getUserId()
        + " for question " + answer.getQuestionId()
        + " - selected option: " + answer.getSelectedOption());

    // Prevent duplicate answers
    if (redisService.hasUserAnswered(quizId, answer.getQuestionId(), answer.getUserId())) {
      System.out.println(" User " + answer.getUserId() + " already answered this question - ignoring duplicate");
      return;
    }

    // Load quiz (must exist now)
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

    boolean isCorrect = question.getCorrectOption().equals(answer.getSelectedOption());

    // Save the raw answer
    redisService.saveAnswer(answer);

    // Update score
    int newScore = redisService.getUserScore(quizId, answer.getUserId());
    if (isCorrect) {
      redisService.incrementUserScore(quizId, answer.getUserId(), 10);
      newScore += 10;
      System.out.println("CORRECT! User " + answer.getUserId() + " earned 10 points. New score: " + newScore);
    } else {
      System.out.println(" WRONG! User " + answer.getUserId() + " selected option "
          + answer.getSelectedOption() + " but correct was " + question.getCorrectOption());
    }

    // Send per-user result
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

    // Broadcast updated leaderboard
    LeaderboardDTO leaderboard = redisService.getLeaderboard(quizId);
    messagingTemplate.convertAndSend(
        "/topic/quiz/" + quizId + "/leaderboard",
        leaderboard
    );

    System.out.println(" Leaderboard updated and broadcast to all participants");
  }

  // PARTICIPANT JOIN
  @MessageMapping("/quiz/{quizId}/join")
  public void handleJoin(@DestinationVariable Long quizId,
                         @Payload String userId) {

    System.out.println(" User " + userId + " joined quiz " + quizId);

    // Ensure quiz cached first so new joiners get correct state
    redisService.ensureQuizCached(quizId);

    redisService.addParticipant(quizId, userId);

    Long participantCount = redisService.getParticipantCount(quizId);
    messagingTemplate.convertAndSend(
        "/topic/quiz/" + quizId + "/participants",
        participantCount
    );

    // Send current leaderboard
    LeaderboardDTO leaderboard = redisService.getLeaderboard(quizId);
    messagingTemplate.convertAndSend(
        "/topic/quiz/" + quizId + "/leaderboard",
        leaderboard
    );

    System.out.println(" User " + userId + " successfully joined. Total participants: " + participantCount);
  }

  // PARTICIPANT LEAVE
  @MessageMapping("/quiz/{quizId}/leave")
  public void handleLeave(@DestinationVariable Long quizId,
                          @Payload String userId) {

    System.out.println("User " + userId + " left quiz " + quizId);

    redisService.removeParticipant(quizId, userId);

    Long participantCount = redisService.getParticipantCount(quizId);
    messagingTemplate.convertAndSend(
        "/topic/quiz/" + quizId + "/participants",
        participantCount
    );
  }

  // ADMIN NEXT QUESTION
  @MessageMapping("/quiz/{quizId}/admin/next-question")
  public void handleNextQuestion(@DestinationVariable Long quizId,
                                 @Payload QuizEventDTO event) {

    System.out.println("Admin advancing to question " + event.getQuestionNumber() + " for quiz " + quizId);

    if (event.getCurrentQuestion() != null) {
      redisService.setCurrentQuestion(quizId, event.getCurrentQuestion().getId());
    }

    messagingTemplate.convertAndSend(
        "/topic/quiz/" + quizId + "/event",
        event
    );

    System.out.println("Question " + event.getQuestionNumber() + " broadcast to all participants");
  }

  // ADMIN END QUIZ
  @MessageMapping("/quiz/{quizId}/admin/end")
  public void handleEndQuiz(@DestinationVariable Long quizId) {

    System.out.println(" Admin ending quiz " + quizId);

    LeaderboardDTO finalLeaderboard = redisService.getLeaderboard(quizId);

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

    System.out.println("Quiz " + quizId + " ended. Final leaderboard sent with "
        + finalLeaderboard.getTotalParticipants() + " participants.");
  }
}
