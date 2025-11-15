package com.nur.livequiz.qms.client;

import com.quizSystem.shared.dto.QuestionDTO;
import com.quizSystem.shared.dto.QuizDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RTSClient {

  private final RestTemplate rest = new RestTemplate();
  // Updated to match RTS controller endpoint
  private final String RTS_URL = "http://rts-service:8083/rts/quiz";

  public void quizStarted(QuizDTO dto){
    rest.postForObject(RTS_URL + "/update", dto, Void.class);
  }

  public void quizUpdated(QuizDTO dto){
    rest.postForObject(RTS_URL + "/update", dto, Void.class);
  }

  public void questionAdded(QuestionDTO dto) {
     rest.postForObject(RTS_URL+"/question-added",dto, Void.class);
  }
}
