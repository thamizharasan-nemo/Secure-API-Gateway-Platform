package com.backend.API_Gateway.filter;


import com.backend.API_Gateway.exception.TooManyRequestsException;
import com.backend.API_Gateway.security.PublicEndPoints;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, RequesterInfo> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_MS = 60_000; // 1minute

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assert auth != null;
        String userId = String.valueOf(auth.getPrincipal());
        long now = Instant.now().toEpochMilli();
        System.out.println("EPOCH MILLI: "+now);

        RequesterInfo info = requestCounts.getOrDefault(
                userId,
                new RequesterInfo(0, now)
        );

        if (now - info.startTime > WINDOW_MS){
            info.count = 0;
            info.startTime = now;
        }

        info.count++;

        if (info.count > MAX_REQUESTS){
            throw new AccessDeniedException("Too many request!, Rest assure for a minute");
        }

        requestCounts.put(userId, info);

        filterChain.doFilter(request, response);
    }

    private static class RequesterInfo{
        int count;
        long startTime;

        public RequesterInfo( int count, long startTime) {
            this.count = count;
            this.startTime = startTime;
        }

    }


    // Skips public uri paths
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return PublicEndPoints.publicEndPointsURLs.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
