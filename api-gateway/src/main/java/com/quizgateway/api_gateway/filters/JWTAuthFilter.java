package com.quizgateway.api_gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JWTAuthFilter implements GlobalFilter, Ordered {

    // Dev-only WebClient. In prod you’d externalize this and use service discovery.
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8081").build();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final var request = exchange.getRequest();
        final var path = request.getURI().getPath();
        final var method = request.getMethod();

        // 1) Always allow preflight (browser CORS check)
        if (HttpMethod.OPTIONS.equals(method)) {
            return chain.filter(exchange);
        }

        // 2) Always allow gateway -> auth-service and actuator
        if (path.startsWith("/auth/") || path.startsWith("/actuator/")) {
            return chain.filter(exchange);
        }

        // 3) If there is NO Authorization header, let it pass (public endpoint or frontend page)
        final var authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        // 4) If there IS a bearer token, verify it with auth-service; on failure return 401
        final var token = authHeader.substring("Bearer ".length());

        return webClient.post()
                .uri("/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"token\":\"" + token + "\"}"))
                .retrieve()
                .toBodilessEntity()
                .then(chain.filter(exchange))
                .onErrorResume(ex -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
