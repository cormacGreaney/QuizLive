package com.quizSystem.shared.dto;

import java.util.List;

public class LeaderboardDTO {
  private Long quizId;
  private List<UserScore> scores;
  private Integer totalParticipants;

  public LeaderboardDTO() {}

  public LeaderboardDTO(Long quizId, List<UserScore> scores, Integer totalParticipants) {
    this.quizId = quizId;
    this.scores = scores;
    this.totalParticipants = totalParticipants;
  }

  public Long getQuizId() { return quizId; }
  public void setQuizId(Long quizId) { this.quizId = quizId; }

  public List<UserScore> getScores() { return scores; }
  public void setScores(List<UserScore> scores) { this.scores = scores; }

  public Integer getTotalParticipants() { return totalParticipants; }
  public void setTotalParticipants(Integer totalParticipants) { this.totalParticipants = totalParticipants; }

  public static class UserScore {
    private String userId;
    private Integer score;
    private Integer rank;

    public UserScore() {}

    public UserScore(String userId, Integer score, Integer rank) {
      this.userId = userId;
      this.score = score;
      this.rank = rank;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
  }
}
