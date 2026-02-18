package com.backend.API_Gateway.config;

import com.backend.API_Gateway.filter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final StringRedisTemplate redisTemplate;
    private final RequestTracingFilter requestTracingFilter;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomAuthenticationEntryPoint authenticationEntryPoint, StringRedisTemplate redisTemplate, RequestTracingFilter requestTracingFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.redisTemplate = redisTemplate;
        this.requestTracingFilter = requestTracingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/token/**", "/health", "/test/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(requestTracingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(redisRateLimitingFilter(redisTemplate), JwtAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public RateLimitingFilter rateLimitingFilter(){
        return new RateLimitingFilter();
    }

    public RedisRateLimitingFilter redisRateLimitingFilter(StringRedisTemplate redisTemplate){
        return new RedisRateLimitingFilter(redisTemplate);
    }
}
