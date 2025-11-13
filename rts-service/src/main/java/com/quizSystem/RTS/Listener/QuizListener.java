package com.quizSystem.RTS.Listener;
import com.quizSystem.RTS.DTO.QuizDTO;
import com.quizSystem.RTS.service.RedisTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.server.WebSocketService;

@Component
public class QuizListener {

  @Autowired
  private RedisTestService redisService;
  @Autowired
  private WebSocketService webSocketService;

  public void handleQuizUpdate(QuizDTO quiz){
    if(quiz==null||quiz.getId()==null){return;}
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
}
