package com.example.authservice.oauth;

import com.example.authservice.config.GoogleOAuthProps;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

  private final GoogleOAuthProps props;
  private final RestClient rest = RestClient.create();

  public GoogleTokenResponse exchangeCodeForTokens(String code, String redirectUri) {
    if (props.getAllowedRedirectUris() == null || !props.getAllowedRedirectUris().contains(redirectUri)) {
      throw new IllegalArgumentException("redirect_uri not allowed");
    }

    LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("code", code);
    form.add("client_id", props.getClientId());
    form.add("client_secret", props.getClientSecret());
    form.add("redirect_uri", redirectUri);
    form.add("grant_type", "authorization_code");

    return rest.post()
        .uri(props.getTokenUri())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .body(GoogleTokenResponse.class);
  }

  public GoogleUserInfo fetchUserInfo(String accessToken) {
    return rest.get()
        .uri(props.getUserInfoUri())
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .body(GoogleUserInfo.class);
  }

  @Data
  public static class GoogleTokenResponse {
    @JsonProperty("access_token") private String accessToken;
    @JsonProperty("expires_in") private Long expiresIn;
    @JsonProperty("refresh_token") private String refreshToken;
    @JsonProperty("scope") private String scope;
    @JsonProperty("token_type") private String tokenType;
    @JsonProperty("id_token") private String idToken;
  }

  @Data
  public static class GoogleUserInfo {
    private String id;
    private String email;
    private Boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String locale;
  }
}
