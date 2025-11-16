package com.quizSystem.RTS.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizSystem.shared.dto.*;
import com.quizSystem.shared.dto.LeaderboardDTO.UserScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;


import java.util.*;
import java.util.stream.Collectors;

//@Service
//public class RedisTestService {
//
//  @Autowired
//  private RedisTemplate<String, Object> redisTemplate;
//
//  private final ObjectMapper objectMapper = new ObjectMapper();
//
//  // ============================================
//  // QUIZ STORAGE
//  // ============================================
//
//  /**
//   * Save a quiz to Redis
//   */
//  public void saveQuiz(QuizDTO quiz) {
//    try {
//      String key = "quiz:" + quiz.getId();
//      String jsonValue = objectMapper.writeValueAsString(quiz);
//      redisTemplate.opsForValue().set(key, jsonValue);
//      System.out.println("✅ Quiz saved to Redis: " + key);
//    } catch (JsonProcessingException e) {
//      System.err.println(" Failed to serialize quiz: " + e.getMessage());
//    }
//  }
//
//  /**
//   * Get a quiz from Redis
//   */
//  public QuizDTO getQuiz(Long quizId) {
//    try {
//      String key = "quiz:" + quizId;
//      String jsonValue = (String) redisTemplate.opsForValue().get(key);
//      if (jsonValue == null) {
//        System.err.println(" Quiz not found in Redis: " + key);
//        return null;
//      }
//      return objectMapper.readValue(jsonValue, QuizDTO.class);
//    } catch (Exception e) {
//      System.err.println(" Failed to deserialize quiz: " + e.getMessage());
//      return null;
//    }
//  }
//
//  // ============================================
//  // CURRENT QUESTION MANAGEMENT
//  // ============================================
//
//  /**
//   * Set the current question for a quiz
//   */
//  public void setCurrentQuestion(Long quizId, Long questionId) {
//    String key = "quiz:" + quizId + ":current-question";
//    redisTemplate.opsForValue().set(key, questionId.toString());
//    System.out.println(" Current question set for quiz " + quizId + ": " + questionId);
//  }
//
//  /**
//   * Get the current question ID for a quiz
//   */
//  public Long getCurrentQuestionId(Long quizId) {
//    String key = "quiz:" + quizId + ":current-question";
//    String value = (String) redisTemplate.opsForValue().get(key);
//    return value != null ? Long.parseLong(value) : null;
//  }
//
//  // ============================================
//  // ANSWER SUBMISSION & STORAGE
//  // ============================================
//
//  /**
//   * Save a participant's answer to Redis
//   * Key format: "quiz:{quizId}:question:{questionId}:answers"
//   * Value: Hash of userId -> selectedOption
//   */
//  public void saveAnswer(AnswerDTO answer) {
//    String key = String.format("quiz:%d:question:%d:answers",
//      answer.getQuizId(),
//      answer.getQuestionId());
//
//    // Store as hash: userId -> selectedOption
//    redisTemplate.opsForHash().put(
//      key,
//      answer.getUserId(),
//      answer.getSelectedOption().toString()
//    );
//
//    System.out.println(" Saved answer from " + answer.getUserId() + " to Redis: " + key);
//  }
//
//  /**
//   * Get all answers for a specific question
//   */
//  public Map<String, Integer> getAnswersForQuestion(Long quizId, Long questionId) {
//    String key = String.format("quiz:%d:question:%d:answers", quizId, questionId);
//    Map<Object, Object> rawAnswers = redisTemplate.opsForHash().entries(key);
//
//    return rawAnswers.entrySet().stream()
//      .collect(Collectors.toMap(
//        e -> e.getKey().toString(),
//        e -> Integer.parseInt(e.getValue().toString())
//      ));
//  }
//
//  /**
//   * Check if user already answered this question
//   */
//  public boolean hasUserAnswered(Long quizId, Long questionId, String userId) {
//    String key = String.format("quiz:%d:question:%d:answers", quizId, questionId);
//    return redisTemplate.opsForHash().hasKey(key, userId);
//  }
//
//  // ============================================
//  // SCORING
//  // ============================================
//
//  /**
//   * Increment user's score
//   */
//  public void incrementUserScore(Long quizId, String userId, int points) {
//    String key = "quiz:" + quizId + ":scores";
//    redisTemplate.opsForHash().increment(key, userId, points);
//    System.out.println("✅ Incremented score for " + userId + " by " + points);
//  }
//
//  /**
//   * Get user's current score
//   */
//  public Integer getUserScore(Long quizId, String userId) {
//    String key = "quiz:" + quizId + ":scores";
//    Object score = redisTemplate.opsForHash().get(key, userId);
//    return score != null ? Integer.parseInt(score.toString()) : 0;
//  }
//
//  /**
//   * Get leaderboard (top scores sorted)
//   */
//  public LeaderboardDTO getLeaderboard(Long quizId) {
//    String key = "quiz:" + quizId + ":scores";
//    Map<Object, Object> allScores = redisTemplate.opsForHash().entries(key);
//
//    if (allScores.isEmpty()) {
//      return new LeaderboardDTO(quizId, new ArrayList<>(), 0);
//    }
//
//    // Convert to sorted list
//    List<UserScore> sortedScores = allScores.entrySet().stream()
//      .map(e -> new UserScore(
//        e.getKey().toString(),
//        Integer.parseInt(e.getValue().toString()),
//        0  // Rank will be set below
//      ))
//      .sorted((a, b) -> b.getScore() - a.getScore())  // Descending order
//      .collect(Collectors.toList());
//
//    // Assign ranks
//    for (int i = 0; i < sortedScores.size(); i++) {
//      sortedScores.get(i).setRank(i + 1);
//    }
//
//    return new LeaderboardDTO(quizId, sortedScores, sortedScores.size());
//  }
//
//  // ============================================
//  // PARTICIPANT TRACKING
//  // ============================================
//
//  /**
//   * Add a participant to the quiz
//   */
//  public void addParticipant(Long quizId, String userId) {
//    String key = "quiz:" + quizId + ":participants";
//    redisTemplate.opsForSet().add(key, userId);
//    System.out.println(" Added participant " + userId + " to quiz " + quizId);
//  }
//
//  /**
//   * Remove a participant from the quiz
//   */
//  public void removeParticipant(Long quizId, String userId) {
//    String key = "quiz:" + quizId + ":participants";
//    redisTemplate.opsForSet().remove(key, userId);
//    System.out.println(" Removed participant " + userId + " from quiz " + quizId);
//  }
//
//  /**
//   * Get count of active participants
//   */
//  public Long getParticipantCount(Long quizId) {
//    String key = "quiz:" + quizId + ":participants";
//    return redisTemplate.opsForSet().size(key);
//  }
//
//  // ============================================
//  // UTILITY
//  // ============================================
//
//  /**
//   * Clear all data for a quiz (when quiz ends)
//   */
//  public void clearQuizData(Long quizId) {
//    String pattern = "quiz:" + quizId + ":*";
//    Set<String> keys = redisTemplate.keys(pattern);
//    if (keys != null && !keys.isEmpty()) {
//      redisTemplate.delete(keys);
//      System.out.println(" Cleared " + keys.size() + " keys for quiz " + quizId);
//    }
//  }
//
//  /**
//   * Test connection
//   */
//  public String testConnection() {
//    try {
//      redisTemplate.opsForValue().set("test-key", "test-value");
//      String result = (String) redisTemplate.opsForValue().get("test-key");
//      return " Redis connection successful! Retrieved: " + result;
//    } catch (Exception e) {
//      return "Redis connection failed: " + e.getMessage();
//    }
//  }
//}
@Service
public class RedisTestService {

 private final StringRedisTemplate redis;
 //private final RedisTemplate<String, Object> redis;
 private final ObjectMapper objectMapper = new ObjectMapper();

  public RedisTestService(StringRedisTemplate redis) {
   this.redis = redis;
  }
 //public RedisTestService(RedisTemplate<String, Object> redis) {
 //  this.redis = redis;

  // =========================
  // QUIZ STORAGE (JSON)
  // =========================
  public void saveQuiz(QuizDTO quiz) {
    try {
      String key = "quiz:" + quiz.getId();
      String json = objectMapper.writeValueAsString(quiz);
      redis.opsForValue().set(key, json);
      System.out.println("✅ Quiz saved to Redis: " + key);
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
          try { return Integer.parseInt(String.valueOf(e.getValue())); }
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

// Replace ONLY the getLeaderboard method with this version
//public LeaderboardDTO getLeaderboard(Long quizId) {
//  String scoresKey = "quiz:" + quizId + ":scores";
//  String participantsKey = "quiz:" + quizId + ":participants";
//
//  // Read scores (hash userId -> numeric string)
//  Map<Object, Object> rawScores = redis.opsForHash().entries(scoresKey);
//  Map<String, Integer> scores = new HashMap<>();
//  for (Map.Entry<Object, Object> e : rawScores.entrySet()) {
//    String user = String.valueOf(e.getKey());
//    int val;
//    try { val = (int) Long.parseLong(String.valueOf(e.getValue())); }
//    catch (NumberFormatException ex) { val = 0; }
//    scores.put(user, val);
//  }
//
//  // Read participants (set of userIds)
//  Set<Object> rawParticipants = redis.opsForSet().members(participantsKey);
//  Set<String> participants = new LinkedHashSet<>();
//  if (rawParticipants != null) {
//    for (Object p : rawParticipants) {
//      participants.add(String.valueOf(p));
//    }
//  }
//
//  // Build rows: include ALL participants with default score 0
//  List<LeaderboardDTO.UserScore> rows = new ArrayList<>();
//  if (!participants.isEmpty()) {
//    for (String user : participants) {
//      rows.add(new LeaderboardDTO.UserScore(user, scores.getOrDefault(user, 0), 0));
//    }
//  } else {
//    // Fallback: if no participants were found, include whoever has scores
//    for (Map.Entry<String, Integer> e : scores.entrySet()) {
//      rows.add(new LeaderboardDTO.UserScore(e.getKey(), e.getValue(), 0));
//    }
//  }
//
//  // Sort by score desc, then userId asc
//  rows.sort((a, b) -> {
//    int byScore = Integer.compare(b.getScore(), a.getScore());
//    return byScore != 0 ? byScore : a.getUserId().compareTo(b.getUserId());
//  });
//
//  // Assign ranks
//  for (int i = 0; i < rows.size(); i++) {
//    rows.get(i).setRank(i + 1);
//  }
//
//  // Total participants from the set
//  int totalParticipants = participants.size();
//
//  return new LeaderboardDTO(quizId, rows, totalParticipants);
//}
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
