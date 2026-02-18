package com.backend.API_Gateway.controllers;

import com.backend.API_Gateway.exception.BadRequestException;
import com.backend.API_Gateway.security.JwtUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtils jwtUtils;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Map<String, String> login() {
        String userId = "Nemo";
        String role = "ROLE_ADMIN";

        return Map.of(
                "ACCESS TOKEN: ", jwtUtils.generateAccessToken(userId, role),
                "REFRESH TOKEN: ", jwtUtils.generateRefreshToken(userId)
        );
    }


    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestHeader("RefreshToken")String header) {
        if (!header.startsWith("Bearer ")){
            throw new BadRequestException("Refresh ain't start with bearer");
        }
        String refreshToken = header.substring(7);

        String userId = jwtUtils.extractUsername(refreshToken);
        String role = "ROLE_ADMIN";

        if(!"REFRESH".equals(jwtUtils.extractType(refreshToken))){
            System.out.println(jwtUtils.extractType("TOKEN TYPE: "+refreshToken));
            throw new BadRequestException("It's not a refresh token");
        }

        return Map.of(
                "ACCESS TOKEN: ",jwtUtils.generateAccessToken(userId, role),
                "REFRESH TOKEN: ", jwtUtils.generateRefreshToken(userId)
        );
    }
}
