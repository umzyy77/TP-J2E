package org.example.tpj2eannonces.core.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtClaimsServiceTest {

    private static final String SECRET = "TestSecretKeyForJWTSigningMustBeAtLeast256BitsLongForTests!!";

    private JwtService jwtService;
    private JwtClaimsService jwtClaimsService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3600000L);
        jwtClaimsService = new JwtClaimsService(jwtService);
    }

    @Test
    void extractUserId_shouldReturnCorrectUUID() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER"), Set.of("ROLE_USER"));

        assertThat(jwtClaimsService.extractUserId(token)).isEqualTo(userId);
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "bob", Set.of("ROLE_USER"), Set.of("ROLE_USER"));

        assertThat(jwtClaimsService.extractUsername(token)).isEqualTo("bob");
    }

    @Test
    void extractRoles_shouldReturnRoleList() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER", "ROLE_ADMIN"), Set.of("ROLE_USER"));

        List<String> roles = jwtClaimsService.extractRoles(token);

        assertThat(roles).contains("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void extractAuthorities_shouldReturnAuthoritiesList() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER"), Set.of("ROLE_USER", "ANNONCE_READ"));

        List<String> authorities = jwtClaimsService.extractAuthorities(token);

        assertThat(authorities).contains("ROLE_USER", "ANNONCE_READ");
    }

    @Test
    void extractRole_shouldReturnFirstRole() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER"), Set.of("ROLE_USER"));

        assertThat(jwtClaimsService.extractRole(token)).isEqualTo("ROLE_USER");
    }

    @Test
    void extractRole_shouldReturnNull_whenNoRoles() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of(), Set.of());

        assertThat(jwtClaimsService.extractRole(token)).isNull();
    }

    @Test
    void extractAuthorities_shouldFallbackToRoles_whenNoAuthoritiesClaim() {
        UUID userId = UUID.randomUUID();
        // When authorities is empty set, it will fallback to extractRoles
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER"), Set.of());

        List<String> authorities = jwtClaimsService.extractAuthorities(token);
        // Should fallback to roles
        assertThat(authorities).contains("ROLE_USER");
    }

    @Test
    void extractRoles_shouldReturnEmpty_whenNoRolesClaim() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of(), Set.of());

        List<String> roles = jwtClaimsService.extractRoles(token);
        assertThat(roles).isEmpty();
    }

    @Test
    void extractAuthorities_shouldReturnAuthoritiesDirectly_whenPresent() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER"), Set.of("ANNONCE_READ", "ANNONCE_WRITE"));

        List<String> authorities = jwtClaimsService.extractAuthorities(token);
        assertThat(authorities).contains("ANNONCE_READ", "ANNONCE_WRITE");
    }

    @Test
    void extractRoles_shouldReturnEmpty_whenSingleRoleClaimIsBlank() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        UUID userId = UUID.randomUUID();
        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("username", "alice")
                .claim("role", "   ")
                .claim("tokenType", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000L))
                .signWith(key)
                .compact();

        List<String> roles = jwtClaimsService.extractRoles(token);
        assertThat(roles).isEmpty();
    }

    @Test
    void extractRoles_shouldReturnEmpty_whenSingleRoleClaimIsNull() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        UUID userId = UUID.randomUUID();
        // No "role" claim at all -> claims.get("role", String.class) returns null
        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("username", "alice")
                .claim("tokenType", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000L))
                .signWith(key)
                .compact();

        List<String> roles = jwtClaimsService.extractRoles(token);
        assertThat(roles).isEmpty();
    }

    @Test
    void extractRoles_shouldSkipEmptyAndBlankValuesInCollection() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        UUID userId = UUID.randomUUID();
        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("username", "alice")
                .claim("roles", Arrays.asList("ROLE_USER", "", "  ", "ROLE_ADMIN"))
                .claim("role", "ROLE_USER")
                .claim("tokenType", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000L))
                .signWith(key)
                .compact();

        List<String> roles = jwtClaimsService.extractRoles(token);
        assertThat(roles).containsExactly("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void extractRoles_shouldFallbackToSingleRoleClaim() {
        // Build a token with only a "role" string claim (no "roles" list)
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        UUID userId = UUID.randomUUID();
        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("username", "alice")
                .claim("role", "ROLE_ADMIN")
                .claim("tokenType", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000L))
                .signWith(key)
                .compact();

        List<String> roles = jwtClaimsService.extractRoles(token);
        assertThat(roles).containsExactly("ROLE_ADMIN");
    }

    @Test
    void extractAuthorities_shouldFallbackToRoles_whenAuthoritiesClaimIsNotCollection() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        UUID userId = UUID.randomUUID();
        // Token with roles list but authorities as a string (not a collection)
        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("username", "alice")
                .claim("roles", List.of("ROLE_USER"))
                .claim("authorities", "not-a-list")
                .claim("role", "ROLE_USER")
                .claim("tokenType", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000L))
                .signWith(key)
                .compact();

        List<String> authorities = jwtClaimsService.extractAuthorities(token);
        // Should fallback to extractRoles
        assertThat(authorities).contains("ROLE_USER");
    }

    @Test
    void extractRoles_shouldSkipNullValuesInCollection() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        UUID userId = UUID.randomUUID();
        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("username", "alice")
                .claim("roles", Arrays.asList("ROLE_USER", null, "ROLE_ADMIN"))
                .claim("role", "ROLE_USER")
                .claim("tokenType", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000L))
                .signWith(key)
                .compact();

        List<String> roles = jwtClaimsService.extractRoles(token);
        assertThat(roles).containsExactly("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void extractRoles_shouldReturnMultipleRoles() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", Set.of("ROLE_USER", "ROLE_ADMIN"), Set.of("ROLE_USER", "ROLE_ADMIN"));

        List<String> roles = jwtClaimsService.extractRoles(token);
        assertThat(roles).contains("ROLE_USER", "ROLE_ADMIN");
    }
}
