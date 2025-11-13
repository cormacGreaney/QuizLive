package com.quizSystem.RTS.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

  private final SimpMessagingTemplate messagingTemplate;

  @Autowired
  public WebSocketService(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Broadcasts a message to all subscribers of a given topic.
   * @param topic The topic to send to, e.g., "/topic/quiz/1"
   * @param payload The object to broadcast (e.g., QuizDTO)
   */
  public void broadcast(String topic, Object payload) {
    messagingTemplate.convertAndSend(topic, payload);
  }
}
