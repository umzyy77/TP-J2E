package org.example.tpj2eannonces.core.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import static org.assertj.core.api.Assertions.assertThat;

class JwtClaimsServiceTest {

    private static final String SECRET = "MasterAnnonceSecretKeyForJWTSigningMustBeAtLeast256BitsLong!!";
    private static final long EXPIRATION_MS = 86_400_000L;

    private JwtClaimsService jwtClaimsService;
    private JwtService jwtService;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);
        jwtClaimsService = new JwtClaimsService(jwtService);
        signingKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldExtractClaimsFromGeneratedToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(
                userId,
                "alice",
                Arrays.asList("ROLE_ADMIN", "ROLE_ADMIN", " ", "ROLE_USER"),
                Arrays.asList("ANNONCE_ARCHIVE", null, " ", "ANNONCE_ARCHIVE")
        );

        assertThat(jwtClaimsService.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtClaimsService.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtClaimsService.extractRoles(token)).containsExactly("ROLE_ADMIN", "ROLE_USER");
        assertThat(jwtClaimsService.extractAuthorities(token)).containsExactly("ANNONCE_ARCHIVE");
        assertThat(jwtClaimsService.extractRole(token)).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldFallbackAuthoritiesToRolesWhenAuthoritiesAreEmpty() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "bob", List.of("ROLE_USER"), List.of());

        assertThat(jwtClaimsService.extractAuthorities(token)).containsExactly("ROLE_USER");
        assertThat(jwtClaimsService.extractRoles(token)).containsExactly("ROLE_USER");
        assertThat(jwtClaimsService.extractRole(token)).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldReturnEmptyClaimsWhenNoRoleInformationExists() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "charlie", List.of(), List.of());

        assertThat(jwtClaimsService.extractRoles(token)).isEmpty();
        assertThat(jwtClaimsService.extractAuthorities(token)).isEmpty();
        assertThat(jwtClaimsService.extractRole(token)).isNull();
    }

    @Test
    void shouldHandleBlankRoleClaimAndReturnEmptyRoleList() {
        String token = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim("username", "blank")
                .claim("role", " ")
                .signWith(signingKey)
                .compact();

        assertThat(jwtClaimsService.extractRoles(token)).isEmpty();
        assertThat(jwtClaimsService.extractAuthorities(token)).isEmpty();
        assertThat(jwtClaimsService.extractRole(token)).isNull();
    }

    @Test
    void shouldFallbackToSingleRoleClaimWhenRolesListIsMissing() {
        String token = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim("username", "single")
                .claim("role", "ROLE_EDITOR")
                .signWith(signingKey)
                .compact();

        assertThat(jwtClaimsService.extractRoles(token)).containsExactly("ROLE_EDITOR");
        assertThat(jwtClaimsService.extractAuthorities(token)).containsExactly("ROLE_EDITOR");
        assertThat(jwtClaimsService.extractRole(token)).isEqualTo("ROLE_EDITOR");
    }

    @Test
    void shouldHandleNonCollectionAuthoritiesClaimAndFilterNullValuesInRolesClaim() {
        String token = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim("username", "delta")
                .claim("role", "ROLE_FALLBACK")
                .claim("roles", Arrays.asList(null, "ROLE_MODERATOR", " "))
                .claim("authorities", "NOT_A_COLLECTION")
                .signWith(signingKey)
                .compact();

        assertThat(jwtClaimsService.extractRoles(token)).containsExactly("ROLE_MODERATOR");
        assertThat(jwtClaimsService.extractAuthorities(token)).containsExactly("ROLE_MODERATOR");
        assertThat(jwtClaimsService.extractRole(token)).isEqualTo("ROLE_MODERATOR");
    }

    @Test
    void shouldReturnEmptyRolesWhenRoleClaimIsMissing() {
        String token = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim("username", "echo")
                .signWith(signingKey)
                .compact();

        assertThat(jwtClaimsService.extractRoles(token)).isEmpty();
        assertThat(jwtClaimsService.extractRole(token)).isNull();
    }
}
