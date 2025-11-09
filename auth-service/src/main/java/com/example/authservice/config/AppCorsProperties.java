package com.example.authservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppCorsProperties {
  @Value("${cors.allowed-origins:*}") private String allowedOrigins;
  @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}") private String allowedMethods;
  @Value("${cors.allowed-headers:*}") private String allowedHeaders;
}
