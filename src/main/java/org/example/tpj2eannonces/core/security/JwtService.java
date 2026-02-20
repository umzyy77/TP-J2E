package org.example.tpj2eannonces.core.security;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.example.tpj2eannonces.shared.util.StringCollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiration;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(UUID userId,
                                String username,
                                Collection<String> roles,
                                Collection<String> authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        List<String> normalizedRoles = StringCollectionUtils.normalizeDistinct(roles);
        List<String> normalizedAuthorities = StringCollectionUtils.normalizeDistinct(authorities);

        if (normalizedAuthorities.isEmpty()) {
            normalizedAuthorities = normalizedRoles;
        }

        JwtBuilder builder = Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("roles", normalizedRoles)
                .claim("authorities", normalizedAuthorities)
                .issuedAt(now)
                .expiration(expiryDate);

        if (!normalizedRoles.isEmpty()) {
            builder.claim("role", normalizedRoles.getFirst());
        }

        return builder
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationMs() {
        return expiration;
    }
}
