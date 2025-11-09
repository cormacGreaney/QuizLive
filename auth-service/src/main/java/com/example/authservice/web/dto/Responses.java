package com.example.authservice.web.dto;

import com.example.authservice.domain.User;

public class Responses {
  public record AuthResponse(String accessToken, String refreshToken, UserProfile profile) {}
  public record UserProfile(Long id, String email, String name, String pictureUrl, String role, String provider) {
    public static UserProfile from(User u) {
      return new UserProfile(u.getId(), u.getEmail(), u.getName(), u.getPictureUrl(), u.getRole().name(), u.getProvider().name());
    }
  }
  public record VerifyResponse(boolean valid, String subject, Long expiresAtEpochSeconds) {}
  public record ErrorResponse(String error, String message) {}
}
