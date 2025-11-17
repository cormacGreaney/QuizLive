package com.quizSystem.RTS.Listener;

import com.quizSystem.shared.dto.QuestionDTO;
import com.quizSystem.shared.dto.QuizDTO;
import com.quizSystem.shared.dto.QuestionDTO;
import com.quizSystem.RTS.service.RedisTestService;
import com.quizSystem.RTS.ws.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizListener {

  private final RedisTestService redisService;
  private final WebSocketService webSocketService;

  /**
   * Handles a quiz update: saves to Redis and broadcasts via WebSocket
   */
  public void handleQuizUpdate(QuizDTO quiz){
    if(quiz == null || quiz.getId() == null){
      System.err.println("Invalid quiz update received - missing ID.");
      return;
    }
    saveQuizToRedis(quiz);
    broadcastQuizUpdate(quiz);
  }

  private void saveQuizToRedis(QuizDTO quiz){
    redisService.saveQuiz(quiz);
  }

  private void broadcastQuizUpdate(QuizDTO quiz){
    String topic = "/topic/quiz/" + quiz.getId();
    webSocketService.broadcast(topic, quiz);
  }

  public void handleQuestionUpdate(QuestionDTO question){
    if(question == null || question.getId() == null){
      System.err.println("Invalid question update received - missing ID.");
      return;
    }
    saveQuestionToRedis(question);
    broadcastQuestionUpdate(question);
  }

  private void saveQuestionToRedis(QuestionDTO question){
    redisService.saveQuestion(question);
  }

  private void broadcastQuestionUpdate(QuestionDTO question){
    String topic = "/topic/quiz/"+question.getQuizId();
  }
}
