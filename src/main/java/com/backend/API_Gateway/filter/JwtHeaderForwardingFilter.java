package com.backend.API_Gateway.filter;

import com.backend.API_Gateway.security.JwtUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtHeaderForwardingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtHeaderForwardingFilter.class);
    private final JwtUtils jwtUtil;

    public JwtHeaderForwardingFilter(JwtUtils jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extractAllClaims(token);

        String userId = String.valueOf(claims.getSubject());
        String role = String.valueOf(claims.get("role"));

        String springRole = "ROLE_" + role;

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-USER-ID", userId)
                .header("X-USER-ROLE", springRole)
                .build();

        log.debug(
                "Gateway forwarding to user context userId = {} and role = {}",
                userId,
                role
        );

        return chain.filter(exchange.mutate()
                        .request(mutatedRequest)
                        .build()
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
