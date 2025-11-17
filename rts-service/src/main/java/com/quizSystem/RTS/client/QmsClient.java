package com.quizSystem.RTS.client;

import com.quizSystem.shared.dto.QuizDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class QmsClient {
  private static final Logger log = LoggerFactory.getLogger(QmsClient.class);

  private final WebClient webClient;

  // Defaults to Docker service name; override with GATEWAY_BASE_URL or gateway.base-url
  public QmsClient(@Value("${gateway.base-url:http://api-gateway:8080}") String baseUrl) {
    this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    log.info("QmsClient using baseUrl={}", baseUrl);
  }

  /** Fetches the public view of a quiz (no auth required). */
  public QuizDTO fetchQuiz(long quizId) {
    return webClient.get()
        .uri("/qms/api/quizzes/{id}", quizId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(QuizDTO.class)
        .block();
  }
}
