package org.example.tpj2eannonces.core.security;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "MasterAnnonceSecretKeyForJWTSigningMustBeAtLeast256BitsLong!!";
    private static final long EXPIRATION_MS = 86_400_000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);
    }

    @Test
    void shouldGenerateAndExtractClaimsWithRolesAndAuthorities() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(
                userId,
                "alice",
                Arrays.asList("ROLE_ADMIN", "ROLE_ADMIN", " ", "ROLE_USER"),
                Arrays.asList("ANNONCE_ARCHIVE", null, " ", "ANNONCE_ARCHIVE")
        );

        Claims claims = jwtService.parseToken(token);

        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("username", String.class)).isEqualTo("alice");
        assertThat(claims.get("roles", List.class)).containsExactly("ROLE_ADMIN", "ROLE_USER");
        assertThat(claims.get("authorities", List.class)).containsExactly("ANNONCE_ARCHIVE");
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldFallbackAuthoritiesToRolesWhenAuthoritiesAreEmpty() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "bob", List.of("ROLE_USER"), List.of());
        Claims claims = jwtService.parseToken(token);

        assertThat(claims.get("roles", List.class)).containsExactly("ROLE_USER");
        assertThat(claims.get("authorities", List.class)).containsExactly("ROLE_USER");
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldNotAddLegacyRoleClaimWhenRolesAreEmpty() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "charlie", List.of(), List.of());
        Claims claims = jwtService.parseToken(token);

        assertThat(claims.get("roles", List.class)).isEmpty();
        assertThat(claims.get("authorities", List.class)).isEmpty();
        assertThat(claims.get("role", String.class)).isNull();
    }

    @Test
    void shouldHandleNullRolesAndAuthoritiesOnTokenGeneration() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "foxtrot", null, null);
        Claims claims = jwtService.parseToken(token);

        assertThat(claims.get("roles", List.class)).isEmpty();
        assertThat(claims.get("authorities", List.class)).isEmpty();
        assertThat(claims.get("role", String.class)).isNull();
        assertThat(jwtService.getExpirationMs()).isEqualTo(EXPIRATION_MS);
    }

    @Test
    void shouldReturnFalseForInvalidOrNullToken() {
        assertThat(jwtService.validateToken("invalid-token")).isFalse();
        assertThat(jwtService.validateToken(null)).isFalse();
    }
}
