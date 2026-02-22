package org.example.tpj2eannonces.core.security;

import java.util.Set;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "TestSecretKeyForJWTSigningMustBeAtLeast256BitsLongForTests!!";
    private static final long EXPIRATION = 3600000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION);
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
        assertThat(jwtService.getExpirationMs()).isEqualTo(EXPIRATION);
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
}
