package com.backend.API_Gateway.filter;

import com.backend.API_Gateway.security.JwtUtils;
import com.backend.API_Gateway.security.PublicEndPoints;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || authorizationHeader.isBlank()){
            throw new BadCredentialsException("Missing Authorization header");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Authorization header should start with Bearer");
        }

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = jwtUtils.extractAllClaims(token);

            if (!"ACCESS".equals(claims.get("type"))){
                throw new BadCredentialsException("It's a REFRESH TOKEN! provide a access token");
            }

            if (jwtUtils.isTokenExpired(token)) { // use jwtUtils.isValid(token) for isolation
                throw new BadCredentialsException("Token has been expired");
            }

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            MDC.put("userId", userId);
            System.out.println("SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            filterChain.doFilter(request, response);
        }
        catch (JwtException je){
//            throw new AuthenticationServiceException("JWT EXCEPTION in JwtAuthenticationFilter ");
            SecurityContextHolder.clearContext();
        }
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
