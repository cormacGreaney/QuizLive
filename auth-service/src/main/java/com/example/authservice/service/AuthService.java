package com.example.authservice.service;

import com.example.authservice.domain.AuthProvider;
import com.example.authservice.domain.Role;
import com.example.authservice.domain.User;
import com.example.authservice.oauth.GoogleOAuthClient;
import com.example.authservice.repo.UserRepository;
import com.example.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service @RequiredArgsConstructor
public class AuthService {
  private final GoogleOAuthClient google;
  private final UserRepository users;
  private final JwtService jwt;

  @Transactional
  public TokenPair loginWithGoogle(String code, String redirectUri) {
    var tokens = google.exchangeCodeForTokens(code, redirectUri);
    var profile = google.fetchUserInfo(tokens.getAccessToken());

    User user = users.findByProviderAndProviderId(AuthProvider.GOOGLE, profile.getId())
      .orElseGet(() -> User.builder()
        .provider(AuthProvider.GOOGLE)
        .providerId(profile.getId())
        .email(profile.getEmail())
        .name(profile.getName() != null ? profile.getName() : profile.getEmail())
        .pictureUrl(profile.getPicture())
        .role(Role.USER)
        .build());

    user.setEmail(profile.getEmail());
    user.setName(profile.getName() != null ? profile.getName() : user.getName());
    user.setPictureUrl(profile.getPicture());
    users.save(user);

    Map<String,Object> claims = new HashMap<>();
    claims.put("role", user.getRole().name());
    claims.put("email", user.getEmail());
    claims.put("name", user.getName());
    claims.put("provider", user.getProvider().name());
    claims.put("uid", user.getId());

    String access = jwt.createAccessToken(user.getId().toString(), claims);
    String refresh = jwt.createRefreshToken(user.getId().toString());
    return new TokenPair(access, refresh, user);
  }

  public record TokenPair(String accessToken, String refreshToken, User user) {}
}
