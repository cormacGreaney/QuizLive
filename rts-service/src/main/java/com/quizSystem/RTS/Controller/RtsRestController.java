package com.quizSystem.RTS.Controller;

import com.quizSystem.RTS.service.RedisTestService;
import com.quizSystem.shared.dto.QuizDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rts")
public class RtsRestController {

  @Autowired
  private RedisTestService redisService;

  public RtsRestController() {
    System.out.println("RtsRestController LOADED!");
  }

  @PostMapping("/quiz/update")
  public ResponseEntity<String> updateQuiz(@RequestBody QuizDTO quiz) {
    System.out.println("📨 Received quiz update from QMS: " + quiz.getId()
      + " (status: " + quiz.getStatus() + ")");

    redisService.saveQuiz(quiz);

    return ResponseEntity.ok("Quiz " + quiz.getId() + " saved to Redis successfully");
  }

  @GetMapping("/health")
  public ResponseEntity<String> health() {
    String redisStatus = redisService.testConnection();
    return ResponseEntity.ok("RTS Service is running. Redis: " + redisStatus);
  }
}
