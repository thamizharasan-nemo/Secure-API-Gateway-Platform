package com.backend.API_Gateway.security;

import java.util.List;

public class PublicEndPoints {

    public static final List<String> publicEndPointsURLs = List.of(
            "/test/**",
            "/auth/**",
            "/token/**",
            "/health"
    );
}
