package com.example.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component @RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      try {
        Jws<Claims> jws = jwtService.parse(token);
        String sub = jws.getBody().getSubject();
        String role = (String) jws.getBody().getOrDefault("role","USER");
        Authentication auth = new AbstractAuthenticationToken(
            List.of(new SimpleGrantedAuthority("ROLE_"+role))) {
          @Override public Object getCredentials() { return token; }
          @Override public Object getPrincipal() { return sub; }
          @Override public boolean isAuthenticated() { return true; }
        };
        ((AbstractAuthenticationToken) auth).setDetails(jws.getBody());
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception ignored) {}
    }
    chain.doFilter(req, res);
  }
}
