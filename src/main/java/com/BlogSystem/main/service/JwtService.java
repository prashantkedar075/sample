package com.BlogSystem.main.service;

import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey secretKey;

    public JwtService() {
        // Generate a secure key for HS256
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // Method to generate a token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10 hours validity
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Method to validate a token
    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return username.equals(extractedUsername) && !isTokenExpired(token);
    }

    // Method to extract username from token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Method to extract expiration date from token
    public Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // Method to check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
