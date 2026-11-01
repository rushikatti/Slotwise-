// src/main/java/com/slotwise/service/JwtService.java
package com.slotwise.service;

import com.slotwise.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,                // ADJUST property name
                      @Value("${jwt.expiration-ms}") long expirationMs) {   // ADJUST property name
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
    var builder = Jwts.builder()
            .subject(user.getEmail())
            .claim("userId", String.valueOf(user.getId()))
            .claim("role", user.getRole().name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs));

    if (user.getTenant() != null) {
        builder.claim("tenantId", String.valueOf(user.getTenant().getId()));
    }
    return builder.signWith(key).compact();
}

    public Long extractUserId(String token) {
    return Long.parseLong(claims(token).get("userId", String.class));
    }

    public Long extractTenantId(String token) {
    String t = claims(token).get("tenantId", String.class);
    return t == null ? null : Long.parseLong(t);   // SUPER_ADMIN has no tenant
    }

        public String extractEmail(String token) {
        return claims(token).getSubject();
    }

    public String extractRole(String token) {
        return claims(token).get("role", String.class);
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }

    public boolean isValid(String token) {
        try { claims(token); return true; }
        catch (Exception e) { return false; }   // bad signature, expired, malformed
    }

    
}