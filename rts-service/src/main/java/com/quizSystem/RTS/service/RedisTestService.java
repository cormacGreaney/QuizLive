package com.quizSystem.RTS.service;
import com.quizSystem.shared.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisTestService {

    private final RedisTemplate<String, Object> redis;

    private String leaderboardKey(String quizId) {
        return "leaderboard:" + quizId;
    }

    public void addScore(String quizId, String player, double score) {
        redis.opsForZSet().incrementScore(leaderboardKey(quizId), player, score);
    }
  public void saveQuiz(QuizDTO quiz) {
    String key = "quiz:" + quiz.getId();
    redis.opsForValue().set(key, quiz);
  }
    public Set<ZSetOperations.TypedTuple<Object>> getTopPlayers(String quizId, int topN) {
        return redis.opsForZSet().reverseRangeWithScores(leaderboardKey(quizId), 0, topN - 1);
    }
}
