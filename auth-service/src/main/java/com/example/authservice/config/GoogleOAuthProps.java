package com.example.authservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "google.oauth")
public class GoogleOAuthProps {
  private String clientId;
  private String clientSecret;
  private String tokenUri;
  private String userInfoUri;
  private List<String> allowedRedirectUris;

  public String getClientId() { return clientId; }
  public void setClientId(String clientId) { this.clientId = clientId; }
  public String getClientSecret() { return clientSecret; }
  public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
  public String getTokenUri() { return tokenUri; }
  public void setTokenUri(String tokenUri) { this.tokenUri = tokenUri; }
  public String getUserInfoUri() { return userInfoUri; }
  public void setUserInfoUri(String userInfoUri) { this.userInfoUri = userInfoUri; }
  public List<String> getAllowedRedirectUris() { return allowedRedirectUris; }
  public void setAllowedRedirectUris(List<String> allowedRedirectUris) { this.allowedRedirectUris = allowedRedirectUris; }
}
