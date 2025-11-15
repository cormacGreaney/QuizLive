package com.nur.livequiz.qms.client;

import com.quizSystem.shared.dto.QuestionDTO;
import com.quizSystem.shared.dto.QuizDTO;
import org.springframework.web.client.RestTemplate;

public class RTSClient {

  private final RestTemplate rest = new RestTemplate();
  private final String RTS_URL = "http://rts-service:8083/internal";

  public void quizStarted (QuizDTO dto){
    rest.postForObject(RTS_URL +"/quiz-started",dto,Void.class);
  }
  public void quizUpdated(QuizDTO dto){
    rest.postForObject(RTS_URL+"/quiz-updated",dto,Void.class);
  }
  public void questionAdded(QuestionDTO dto) {
    rest.postForObject(RTS_URL + "/question-added",dto,Void.class);
  }
}
