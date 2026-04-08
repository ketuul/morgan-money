package com.ketul.morganmoney.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // This is the secret key used to sign tokens
    // In a real app this would be stored in AWS Secrets Manager
    private static final String SECRET = "morganmoney-super-secret-key-that-is-long-enough-for-hs256";

    // Tokens expire after 24 hours
    private static final long EXPIRATION_MS = 86400000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Create a token for a user — called when they log in
    public String generateToken(String email) {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    // Read the email out of a token — called on every request
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    // Check if a token is valid and not expired
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
