package com.backend.API_Gateway.controllers;

import com.backend.API_Gateway.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/test")
    String forTestPurpose(){
        System.out.println();
        return "GateWay is working!";
    }

    @GetMapping("/context/security-holder")
    String getSecurityContextHolder(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return "Hello " + authentication.getPrincipal();
    }

    @GetMapping("/token/generate/jwt-token")
    String generateToken(){
        return jwtUtils.generateAccessToken("Shivani", "ROLE_USER");
    }
}
