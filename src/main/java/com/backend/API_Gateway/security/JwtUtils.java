package com.backend.API_Gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.accessTokenExpirationInSec}")
    private long accessTokenExpirationInSec; // 1 day in seconds

    @Value("${jwt.refreshTokenExpirationInSec}")
    private long refreshTokenExpirationInSec; // 5 days in seconds

    public Key getSigninKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateAccessToken(String userId, String role){
        return Jwts
                .builder()
                .subject(userId)
                .claim("role", role)
                .claim("type", "ACCESS")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationInSec * 1000))
                .signWith(getSigninKey())
                .compact();
    }

    public String generateRefreshToken(String userId){
        return Jwts
                .builder()
                .subject(userId)
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationInSec * 1000))
                .signWith(getSigninKey())
                .compact();
    }

    public Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token){
        return extractClaim(token, claims -> claims.get("role").toString());
    }

    public String extractType(String token){
        return extractClaim(token, claims -> claims.get("type").toString());
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isValid(String token){
        return extractUsername(token) != null && !isTokenExpired(token);
    }


}
