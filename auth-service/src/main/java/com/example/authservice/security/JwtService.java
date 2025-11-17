package com.example.authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service @RequiredArgsConstructor
public class JwtService {
  @Value("${auth.jwt.secret}") private String secret;
  @Value("${auth.jwt.issuer}") private String issuer;
  @Value("${auth.jwt.accessTokenTtlMinutes}") private long accessTokenTtlMinutes;
  @Value("${auth.jwt.refreshTokenTtlDays}") private long refreshTokenTtlDays;
  private Key key;

  @PostConstruct void init() { this.key = Keys.hmacShaKeyFor(secret.getBytes()); }

  public String createAccessToken(String subject, Map<String,Object> claims) {
    Instant now = Instant.now();
    return Jwts.builder()
      .setIssuer(issuer).setSubject(subject)
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plusSeconds(accessTokenTtlMinutes*60)))
      .addClaims(claims)
      .signWith(key, SignatureAlgorithm.HS256).compact();
  }

  public String createRefreshToken(String subject) {
    Instant now = Instant.now();
    return Jwts.builder()
      .setIssuer(issuer).setSubject(subject)
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plusSeconds(refreshTokenTtlDays*24*3600)))
      .claim("typ","refresh")
      .signWith(key, SignatureAlgorithm.HS256).compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).requireIssuer(issuer).build().parseClaimsJws(token);
  }

  public boolean isRefreshToken(String token) {
    try { return "refresh".equals(parse(token).getBody().get("typ")); }
    catch (JwtException e) { return false; }
  }
}
