package com.example.authservice.web;

import com.example.authservice.security.JwtService;
import com.example.authservice.service.AuthService;
import com.example.authservice.web.dto.Requests.GoogleLoginRequest;
import com.example.authservice.web.dto.Requests.RefreshTokenRequest;
import com.example.authservice.web.dto.Requests.VerifyTokenRequest;
import com.example.authservice.web.dto.Responses.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final JwtService jwt;

  @PostMapping("/login/google")
  public AuthResponse loginGoogle(@Valid @RequestBody GoogleLoginRequest req) {
    var pair = authService.loginWithGoogle(req.code(), req.redirectUri());
    return new AuthResponse(pair.accessToken(), pair.refreshToken(), UserProfile.from(pair.user()));
  }

  @PostMapping("/refresh")
  public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest req) {
    if (!jwt.isRefreshToken(req.refreshToken())) throw new IllegalArgumentException("Invalid refresh token");
    Jws<io.jsonwebtoken.Claims> jws = jwt.parse(req.refreshToken());
    String userId = jws.getBody().getSubject();
    String newAccess = jwt.createAccessToken(userId, Map.of());
    String newRefresh = jwt.createRefreshToken(userId);
    return new AuthResponse(newAccess, newRefresh, null);
  }

  @PostMapping("/verify")
  public VerifyResponse verify(@Valid @RequestBody VerifyTokenRequest req) {
    try {
      Jws<Claims> jws = jwt.parse(req.token());
      long exp = jws.getBody().getExpiration().toInstant().getEpochSecond();
      return new VerifyResponse(true, jws.getBody().getSubject(), exp);
    } catch (Exception e) { return new VerifyResponse(false, null, null); }
  }

  @GetMapping("/me")
  public UserProfile me(Authentication auth, @RequestHeader(value=HttpHeaders.AUTHORIZATION, required=false) String authz) {
    if (auth == null || auth.getPrincipal() == null) throw new UnauthorizedException("Missing or invalid bearer token");
    Claims claims = (Claims) ((org.springframework.security.authentication.AbstractAuthenticationToken) auth).getDetails();
    Long id = claims.get("uid", Integer.class) != null ? claims.get("uid", Integer.class).longValue() : Long.valueOf((String) auth.getPrincipal());
    String email = (String) claims.getOrDefault("email", null);
    String name = (String) claims.getOrDefault("name", null);
    String picture = (String) claims.getOrDefault("pictureUrl", null);
    String role = (String) claims.getOrDefault("role", "USER");
    String provider = (String) claims.getOrDefault("provider", "GOOGLE");
    return new UserProfile(id, email, name, picture, role, provider);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  private static class UnauthorizedException extends RuntimeException { public UnauthorizedException(String msg){super(msg);} }
}
