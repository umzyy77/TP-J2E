package org.example.tpj2eannonces.core.security;

import java.util.Set;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String ACCESS_SECRET = "TestSecretKeyForJWTSigningMustBeAtLeast256BitsLongForTests!!";
    private static final String REFRESH_SECRET = "TestRefreshSecretKeyForJWTSigningMustBeAtLeast256BitsLong!!!";
    private static final long ACCESS_EXPIRATION = 3600000L;
    private static final long REFRESH_EXPIRATION = 604800000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(ACCESS_SECRET, ACCESS_EXPIRATION, REFRESH_SECRET, REFRESH_EXPIRATION);
    }

    @Test
    void generateToken_shouldProduceValidToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER"), Set.of("ROLE_USER", "ANNONCE_READ"));

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    void parseToken_shouldExtractSubject() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER"), Set.of("ROLE_USER"));

        Claims claims = jwtService.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("username", String.class)).isEqualTo("alice");
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsInvalid() {
        assertThat(jwtService.validateToken("invalid-token")).isFalse();
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsNull() {
        assertThat(jwtService.validateToken(null)).isFalse();
    }

    @Test
    void getExpirationMs_shouldReturnConfiguredValue() {
        assertThat(jwtService.getExpirationMs()).isEqualTo(ACCESS_EXPIRATION);
    }

    @Test
    void getRefreshExpirationMs_shouldReturnConfiguredValue() {
        assertThat(jwtService.getRefreshExpirationMs()).isEqualTo(REFRESH_EXPIRATION);
    }

    @Test
    void generateToken_shouldHandleEmptyRoles() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of(), Set.of("ANNONCE_READ"));

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    void generateToken_shouldHandleEmptyAuthorities() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER"), Set.of());

        assertThat(token).isNotBlank();
        Claims claims = jwtService.parseToken(token);
        // When authorities empty, should use roles as fallback
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_USER");
    }

    @Test
    void generateToken_shouldHandleBothEmpty() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of(), Set.of());

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    void generateRefreshToken_shouldProduceValidRefreshToken() {
        UUID userId = UUID.randomUUID();
        String refreshToken = jwtService.generateRefreshToken(userId, "alice");

        assertThat(refreshToken).isNotBlank();
        assertThat(jwtService.validateRefreshToken(refreshToken)).isTrue();
        assertThat(jwtService.extractUserIdFromRefreshToken(refreshToken)).isEqualTo(userId);
    }

    @Test
    void validateToken_shouldRejectRefreshToken() {
        UUID userId = UUID.randomUUID();
        String refreshToken = jwtService.generateRefreshToken(userId, "alice");

        assertThat(jwtService.validateToken(refreshToken)).isFalse();
    }

    @Test
    void validateRefreshToken_shouldRejectAccessToken() {
        UUID userId = UUID.randomUUID();
        String accessToken = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER"), Set.of("ROLE_USER"));

        assertThat(jwtService.validateRefreshToken(accessToken)).isFalse();
    }
}
