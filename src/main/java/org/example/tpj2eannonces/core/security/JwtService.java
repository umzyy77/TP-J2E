package org.example.tpj2eannonces.core.security;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.example.tpj2eannonces.shared.util.StringCollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    @Autowired
    public JwtService(@Value("${jwt.secret}") String accessSecret,
                      @Value("${jwt.expiration}") long accessExpiration,
                      @Value("${jwt.refresh-secret:${jwt.secret}}") String refreshSecret,
                      @Value("${jwt.refresh-expiration:${jwt.expiration}}") long refreshExpiration) {
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    JwtService(String accessSecret, long accessExpiration) {
        this(accessSecret, accessExpiration, accessSecret, accessExpiration);
    }

    public String generateToken(UUID userId,
                                String username,
                                Collection<String> roles,
                                Collection<String> authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpiration);
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
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .issuedAt(now)
                .expiration(expiryDate);

        if (!normalizedRoles.isEmpty()) {
            builder.claim("role", normalizedRoles.getFirst());
        }

        return builder
                .signWith(accessKey)
                .compact();
    }

    public String generateRefreshToken(UUID userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(refreshKey)
                .compact();
    }

    public Claims parseToken(String token) {
        Claims claims = parseClaims(token, accessKey);
        validateTokenType(claims, ACCESS_TOKEN_TYPE);
        return claims;
    }

    public Claims parseRefreshToken(String token) {
        Claims claims = parseClaims(token, refreshKey);
        validateTokenType(claims, REFRESH_TOKEN_TYPE);
        return claims;
    }

    public UUID extractUserIdFromRefreshToken(String token) {
        return UUID.fromString(parseRefreshToken(token).getSubject());
    }

    private Claims parseClaims(String token, SecretKey key) {
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
        } catch (JwtException | IllegalArgumentException _) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            parseRefreshToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException _) {
            return false;
        }
    }

    private void validateTokenType(Claims claims, String expectedType) {
        String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
        if (!expectedType.equals(tokenType)) {
            throw new JwtException("Type de token invalide");
        }
    }

    public long getExpirationMs() {
        return accessExpiration;
    }

    public long getRefreshExpirationMs() {
        return refreshExpiration;
    }
}
