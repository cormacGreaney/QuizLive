package com.example.authservice.web.dto;

import jakarta.validation.constraints.NotBlank;

public class Requests {
  public record GoogleLoginRequest(@NotBlank String code, @NotBlank String redirectUri) {}
  public record VerifyTokenRequest(@NotBlank String token) {}
  public record RefreshTokenRequest(@NotBlank String refreshToken) {}
}
