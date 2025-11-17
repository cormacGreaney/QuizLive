package com.quizSystem.RTS.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizSystem.RTS.client.QmsClient;
import com.quizSystem.shared.dto.*;
import com.quizSystem.shared.dto.LeaderboardDTO.UserScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RedisTestService {

  private final StringRedisTemplate redis;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private QmsClient qmsClient;

  public RedisTestService(StringRedisTemplate redis) {
    this.redis = redis;
  }

  // =============== NEW ===============
  /** If quiz JSON is not in Redis, fetch from QMS via gateway and cache it. */
  public void ensureQuizCached(Long quizId) {
    String key = "quiz:" + quizId;
    String existing = redis.opsForValue().get(key);
    if (existing != null) return;
    try {
      QuizDTO dto = qmsClient.fetchQuiz(quizId);
      if (dto != null && dto.getId() != null) {
        saveQuiz(dto);
        System.out.println("ensureQuizCached: cached quiz " + quizId + " from QMS");
      } else {
        System.err.println("ensureQuizCached: QMS returned null for quiz " + quizId);
      }
    } catch (Exception e) {
      System.err.println("ensureQuizCached failed for quiz " + quizId + ": " + e.getMessage());
    }
  }
  // ===================================

  // =========================
  // QUIZ STORAGE (JSON)
  // =========================
  public void saveQuiz(QuizDTO quiz) {
    try {
      String key = "quiz:" + quiz.getId();
      String json = objectMapper.writeValueAsString(quiz);
      redis.opsForValue().set(key, json);
      System.out.println("Quiz saved to Redis: " + key);
    } catch (Exception e) {
      System.err.println(" Failed to serialize quiz: " + e.getMessage());
    }
  }

  public QuizDTO getQuiz(Long quizId) {
    try {
      String key = "quiz:" + quizId;
      String json = redis.opsForValue().get(key);
      if (json == null) {
        System.err.println(" Quiz not found in Redis: " + key);
        return null;
      }
      return objectMapper.readValue(json, QuizDTO.class);
    } catch (Exception e) {
      System.err.println(" Failed to deserialize quiz: " + e.getMessage());
      return null;
    }
  }

  // =========================
  // CURRENT QUESTION
  // =========================
  public void setCurrentQuestion(Long quizId, Long questionId) {
    String key = "quiz:" + quizId + ":current-question";
    redis.opsForValue().set(key, String.valueOf(questionId));
    System.out.println(" Current question set for quiz " + quizId + ": " + questionId);
  }

  public Long getCurrentQuestionId(Long quizId) {
    String key = "quiz:" + quizId + ":current-question";
    String value = redis.opsForValue().get(key);
    return (value != null) ? Long.parseLong(value) : null;
  }

  // =========================
  // ANSWERS (HASH userId -> selectedOption)
  // =========================
  public void saveAnswer(AnswerDTO answer) {
    String key = String.format("quiz:%d:question:%d:answers", answer.getQuizId(), answer.getQuestionId());
    redis.opsForHash().put(key, answer.getUserId(), String.valueOf(answer.getSelectedOption()));
    System.out.println(" Saved answer from " + answer.getUserId() + " to Redis: " + key);
  }

  public Map<String, Integer> getAnswersForQuestion(Long quizId, Long questionId) {
    String key = String.format("quiz:%d:question:%d:answers", quizId, questionId);
    Map<Object, Object> raw = redis.opsForHash().entries(key);
    return raw.entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey().toString(),
            e -> {
              try { return (int) Long.parseLong(String.valueOf(e.getValue())); }
              catch (NumberFormatException nfe) { return 0; }
            }
        ));
  }

  public boolean hasUserAnswered(Long quizId, Long questionId, String userId) {
    String key = String.format("quiz:%d:question:%d:answers", quizId, questionId);
    Boolean has = redis.opsForHash().hasKey(key, userId);
    return has != null && has;
  }

  // =========================
  // SCORING (HASH userId -> score)
  // =========================
  public void incrementUserScore(Long quizId, String userId, int points) {
    String key = "quiz:" + quizId + ":scores";
    Long newScore = redis.opsForHash().increment(key, userId, points);
    System.out.println("✅ Incremented score for " + userId + " by " + points + " (now " + newScore + ")");
  }

  public int getUserScore(Long quizId, String userId) {
    Object v = redis.opsForHash().get("quiz:" + quizId + ":scores", userId);
    if (v == null) return 0;
    try { return (int) Long.parseLong(String.valueOf(v)); }
    catch (NumberFormatException e) { return 0; }
  }

  public LeaderboardDTO getLeaderboard(Long quizId) {
    String key = "quiz:" + quizId + ":scores";
    Map<Object, Object> allScores = redis.opsForHash().entries(key);

    List<UserScore> sortedScores = allScores.entrySet().stream()
        .map(e -> {
          int score;
          try { score = (int) Long.parseLong(String.valueOf(e.getValue())); }
          catch (NumberFormatException ex) { score = 0; }
          return new UserScore(e.getKey().toString(), score, 0);
        })
        .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
        .collect(Collectors.toList());

    // assign ranks
    for (int i = 0; i < sortedScores.size(); i++) {
      sortedScores.get(i).setRank(i + 1);
    }

    // IMPORTANT: use participants set size, not number of scored users
    int totalParticipants = Math.toIntExact(getParticipantCount(quizId));

    return new LeaderboardDTO(quizId, sortedScores, totalParticipants);
  }

  // =========================
  // PARTICIPANTS (SET)
  // =========================
  public void addParticipant(Long quizId, String userId) {
    redis.opsForSet().add("quiz:" + quizId + ":participants", userId);
    System.out.println(" Added participant " + userId + " to quiz " + quizId);
  }

  public void removeParticipant(Long quizId, String userId) {
    redis.opsForSet().remove("quiz:" + quizId + ":participants", userId);
    System.out.println(" Removed participant " + userId + " from quiz " + quizId);
  }

  public Long getParticipantCount(Long quizId) {
    Long size = redis.opsForSet().size("quiz:" + quizId + ":participants");
    return size != null ? size : 0L;
  }

  // =========================
  // UTILITY
  // =========================
  public void clearQuizData(Long quizId) {
    String pattern = "quiz:" + quizId + ":*";
    Set<String> keys = redis.keys(pattern);
    if (keys != null && !keys.isEmpty()) {
      redis.delete(keys);
      System.out.println(" Cleared " + keys.size() + " keys for quiz " + quizId);
    }
  }

  public String testConnection() {
    try {
      redis.opsForValue().set("test-key", "test-value");
      String result = redis.opsForValue().get("test-key");
      return " Redis connection successful! Retrieved: " + result;
    } catch (Exception e) {
      return "Redis connection failed: " + e.getMessage();
    }
  }
}
