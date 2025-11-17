package com.quizgateway.api_gateway.filters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class JWTAuthFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(JWTAuthFilter.class);
    private final WebClient webClient = WebClient.builder().baseUrl("http://auth-service").build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Pattern PUBLIC_QMS_GET = Pattern.compile("^/qms/api/quizzes/\\d+$");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final var request = exchange.getRequest();
        final String path = request.getURI().getPath();
        final var method = request.getMethod();

        // CORS preflight & public services
        if (HttpMethod.OPTIONS.equals(method)) return chain.filter(exchange);
        if (path.startsWith("/auth/") || path.startsWith("/actuator/")) return chain.filter(exchange);

        // Only protect /qms/**
        if (!path.startsWith("/qms/")) return chain.filter(exchange);

        // Allow anonymous GET for the participant endpoint
        if (HttpMethod.GET.equals(method) && PUBLIC_QMS_GET.matcher(path).matches()) {
            return chain.filter(exchange);
        }

        // From here on, /qms/** requires a bearer token
        final String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("QMS protected request without bearer: {} {}", method, path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        final String token = authHeader.substring("Bearer ".length());

        return webClient.post()
                .uri("/auth/verify")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of("token", token)))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(body -> {
                    final String uid = extractUidFromVerify(body);
                    if (uid != null) {
                        return proceedWithUid(uid, body.get("email"), body.get("role"), exchange, chain);
                    }
                    final String decodedUid = decodeUidFromJwt(token);
                    if (decodedUid != null) {
                        log.info("Verify missing uid; fallback decode uid={}", decodedUid);
                        return proceedWithUid(decodedUid, null, "USER", exchange, chain);
                    }
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                })
                .onErrorResume(ex -> {
                    final String decodedUid = decodeUidFromJwt(token);
                    if (decodedUid != null) {
                        log.info("Verify error {}; fallback decode uid={}", ex.getClass().getSimpleName(), decodedUid);
                        return proceedWithUid(decodedUid, null, "USER", exchange, chain);
                    }
                    log.error("Verify error and decode failed for {} {}: {}", method, path, ex.toString());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    private Mono<Void> proceedWithUid(String uid, Object emailObj, Object roleObj,
                                      ServerWebExchange exchange, GatewayFilterChain chain) {
        final String email = emailObj != null ? String.valueOf(emailObj) : null;
        final String role = String.valueOf(Objects.requireNonNullElse(roleObj, "USER"));

        ServerHttpRequest mutatedReq = exchange.getRequest().mutate()
                .headers(h -> {
                    h.add("X-User-Id", uid);
                    if (email != null) h.add("X-User-Email", email);
                    h.add("X-User-Role", role);
                })
                .build();
        ServerWebExchange mutatedEx = exchange.mutate().request(mutatedReq).build();
        return chain.filter(mutatedEx);
    }

    private String extractUidFromVerify(Map<?, ?> body) {
        Object uid = body.get("uid");
        if (uid == null) uid = body.get("subject");
        if (uid == null) uid = body.get("sub");
        return uid != null ? String.valueOf(uid) : null;
    }

    private String decodeUidFromJwt(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            Map<String, Object> claims = MAPPER.readValue(new String(decoded, StandardCharsets.UTF_8),
                    new TypeReference<>() {});
            Object uid = claims.get("uid");
            if (uid == null) uid = claims.get("sub");
            return uid != null ? String.valueOf(uid) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override public int getOrder() { return 0; }
}
