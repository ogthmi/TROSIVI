package com.n20.qlphongtro.authservice.service;

import com.n20.qlphongtro.authservice.config.JwtConfig;
import com.n20.qlphongtro.authservice.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;
    private final SecretKey accessKey;

    @Getter
    private final SecretKey refreshKey;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.accessKey = Keys.hmacShaKeyFor(
                jwtConfig.getAccessTokenSecret().getBytes(StandardCharsets.UTF_8)
        );
        this.refreshKey = Keys.hmacShaKeyFor(
                jwtConfig.getRefreshTokenSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(User user) {
        long expireMillis = jwtConfig.getAccessTokenExpireMinutes() * 60L * 1000L;
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("roles", user.getUserRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireMillis))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        long expireMillis = jwtConfig.getRefreshTokenExpireDays() * 24L * 60L * 60L * 1000L;
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireMillis))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    private boolean validateToken(String token, SecretKey key){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token, SecretKey key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }
}