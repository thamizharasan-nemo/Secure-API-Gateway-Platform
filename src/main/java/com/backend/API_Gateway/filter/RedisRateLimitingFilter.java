package com.backend.API_Gateway.filter;

import com.backend.API_Gateway.security.PublicEndPoints;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.concurrent.TimeUnit;

// Bean already defined inside SecurityConfig
public class RedisRateLimitingFilter extends OncePerRequestFilter {
    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_SECONDS = 60; // Fixed window of 60 sec

    private final StringRedisTemplate redisTemplate;

    public RedisRateLimitingFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()){
            throw new BadCredentialsException("Unauthenticated");
        }

        String userId = String.valueOf(auth.getPrincipal());
        String key = "user_id:"+userId;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1){
            redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        System.out.println("Token used: "+count);


        if (count != null && count > MAX_REQUESTS){
            throw new AccessDeniedException("Limit exceeds");
        }

        filterChain.doFilter(request, response);
    }

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return PublicEndPoints.publicEndPointsURLs
                .stream()
                .anyMatch(url -> pathMatcher.match(url, path));
    }
}
