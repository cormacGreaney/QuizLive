package com.quizSystem.RTS.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

  private final RedisTemplate<String, Object> redis;

  private String leaderboardKey(String quizId) {
    return "leaderboard:" + quizId;
  }

  /**
   * Initialize a participant with 0 score when they join
   */
  public void initializeParticipant(String quizId, String nickname) {
    String key = leaderboardKey(quizId);
    redis.opsForZSet().add(key, nickname, 0.0);
    redis.expire(key, 4, TimeUnit.HOURS);
    log.info("Initialized {} in quiz {} with 0 points", nickname, quizId);
  }

  /**
   * Add points to a participant's score
   */
  public Double addScore(String quizId, String nickname, double points) {
    String key = leaderboardKey(quizId);
    Double newScore = redis.opsForZSet().incrementScore(key, nickname, points);
    log.info("Added {} points to {} in quiz {}. New score: {}",
      points, nickname, quizId, newScore);
    return newScore;
  }

  /**
   * Get participant's current rank (1-based)
   */
  public Integer getRank(String quizId, String nickname) {
    String key = leaderboardKey(quizId);
    Long rank = redis.opsForZSet().reverseRank(key, nickname);
    return rank != null ? rank.intValue() + 1 : null;
  }

  /**
   * Get participant's current score
   */
  public Double getScore(String quizId, String nickname) {
    String key = leaderboardKey(quizId);
    return redis.opsForZSet().score(key, nickname);
  }

  /**
   * Get top N participants
   */
  public Map<String, Object> getTopN(String quizId, int n) {
    String key = leaderboardKey(quizId);
    Set<ZSetOperations.TypedTuple<Object>> results =
      redis.opsForZSet().reverseRangeWithScores(key, 0, n - 1);

    return buildLeaderboardResponse(quizId, results);
  }

  /**
   * Get full leaderboard (all participants)
   */
  public Map<String, Object> getFullLeaderboard(String quizId) {
    String key = leaderboardKey(quizId);
    Set<ZSetOperations.TypedTuple<Object>> results =
      redis.opsForZSet().reverseRangeWithScores(key, 0, -1);

    return buildLeaderboardResponse(quizId, results);
  }

  /**
   * Count total participants
   */
  public long getParticipantCount(String quizId) {
    String key = leaderboardKey(quizId);
    Long size = redis.opsForZSet().size(key);
    return size != null ? size : 0;
  }

  /**
   * Clear leaderboard (for testing or reset)
   */
  public void clearLeaderboard(String quizId) {
    String key = leaderboardKey(quizId);
    redis.delete(key);
    log.info("Cleared leaderboard for quiz {}", quizId);
  }

  /**
   * Build structured JSON response
   */
  private Map<String, Object> buildLeaderboardResponse(
    String quizId,
    Set<ZSetOperations.TypedTuple<Object>> results) {

    List<Map<String, Object>> entries = new ArrayList<>();
    int rank = 1;

    if (results != null) {
      for (ZSetOperations.TypedTuple<Object> tuple : results) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("rank", rank++);
        entry.put("nickname", tuple.getValue());
        entry.put("score", tuple.getScore() != null ? tuple.getScore() : 0.0);
        entries.add(entry);
      }
    }

    Map<String, Object> response = new HashMap<>();
    response.put("quizId", quizId);
    response.put("entries", entries);
    response.put("totalParticipants", entries.size());
    response.put("timestamp", System.currentTimeMillis());

    return response;
  }
}
