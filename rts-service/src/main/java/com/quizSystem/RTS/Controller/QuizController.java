package com.quizSystem.RTS.Controller;

import com.quizSystem.RTS.Listener.QuizListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.quizSystem.shared.dto.QuizDTO;
import com.quizSystem.shared.dto.QuestionDTO;


@RestController
@RequestMapping("/rts/quiz")
public class QuizController {

  @Autowired
  private QuizListener quizListener;

  // Endpoint for QMS to push quiz updates
  @PostMapping("/update")
  public ResponseEntity<String> updateQuiz(@RequestBody QuizDTO quizDTO) {
    quizListener.receiveQuizUpdate(quizDTO);
    return ResponseEntity.ok("Quiz update received");
  }
}

