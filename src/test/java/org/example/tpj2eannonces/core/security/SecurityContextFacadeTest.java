package org.example.tpj2eannonces.core.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;

import org.example.tpj2eannonces.core.security.exception.UnauthenticatedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityContextFacadeTest {

    private final SecurityContextFacade facade = new SecurityContextFacade();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireCurrentUserId_shouldReturnUUID_whenPrincipalIsUUIDString() {
        UUID userId = UUID.randomUUID();
        setAuthentication(userId.toString(), "ROLE_USER");

        assertThat(facade.requireCurrentUserId()).isEqualTo(userId);
    }

    @Test
    void requireCurrentUserId_shouldThrow_whenNoAuthentication() {
        assertThatThrownBy(facade::requireCurrentUserId)
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void requireCurrentUserId_shouldThrow_whenPrincipalIsNotUUID() {
        setAuthentication("not-a-uuid", "ROLE_USER");

        assertThatThrownBy(facade::requireCurrentUserId)
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void hasAnyAuthority_shouldReturnTrue_whenAuthorityMatches() {
        setAuthentication(UUID.randomUUID().toString(), "ROLE_USER", "ANNONCE_READ");

        assertThat(facade.hasAnyAuthority("ANNONCE_READ")).isTrue();
    }

    @Test
    void hasAnyAuthority_shouldReturnFalse_whenNoMatch() {
        setAuthentication(UUID.randomUUID().toString(), "ROLE_USER");

        assertThat(facade.hasAnyAuthority("ROLE_ADMIN")).isFalse();
    }

    @Test
    void hasAnyAuthority_shouldReturnFalse_whenNoAuthentication() {
        assertThat(facade.hasAnyAuthority("ROLE_USER")).isFalse();
    }

    @Test
    void hasAnyAuthority_shouldReturnFalse_whenNullArgs() {
        assertThat(facade.hasAnyAuthority((String[]) null)).isFalse();
    }

    @Test
    void hasAnyAuthority_shouldReturnFalse_whenEmptyArgs() {
        assertThat(facade.hasAnyAuthority()).isFalse();
    }

    @Test
    void hasAnyAuthority_shouldReturnFalse_whenBlankAuthorities() {
        setAuthentication(UUID.randomUUID().toString(), "ROLE_USER");
        assertThat(facade.hasAnyAuthority("  ", "")).isFalse();
    }

    @Test
    void hasAnyAuthority_shouldFilterNullExpectedAuthority() {
        setAuthentication(UUID.randomUUID().toString(), "ROLE_USER");
        assertThat(facade.hasAnyAuthority(null, "ROLE_USER")).isTrue();
    }

    @Test
    void hasAnyAuthority_shouldReturnFalse_whenOnlyNullExpected() {
        setAuthentication(UUID.randomUUID().toString(), "ROLE_USER");
        assertThat(facade.hasAnyAuthority(new String[]{null})).isFalse();
    }

    @Test
    void hasAnyAuthority_shouldHandleNullGrantedAuthority() {
        // Create auth with a GrantedAuthority that returns null
        Collection<GrantedAuthority> authorities = List.of(
                (GrantedAuthority) () -> null,
                (GrantedAuthority) () -> "  ",
                new SimpleGrantedAuthority("ROLE_USER")
        );
        var auth = new UsernamePasswordAuthenticationToken(
                UUID.randomUUID().toString(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(facade.hasAnyAuthority("ROLE_USER")).isTrue();
    }

    @Test
    void hasAnyAuthority_shouldReturnFalse_whenAllGrantedAuthoritiesAreNullOrBlank() {
        Collection<GrantedAuthority> authorities = List.of(
                (GrantedAuthority) () -> null,
                (GrantedAuthority) () -> "  "
        );
        var auth = new UsernamePasswordAuthenticationToken(
                UUID.randomUUID().toString(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(facade.hasAnyAuthority("ROLE_USER")).isFalse();
    }

    @Test
    void requireCurrentUserId_shouldReturnUUID_whenPrincipalIsUUID() {
        UUID userId = UUID.randomUUID();
        var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(facade.requireCurrentUserId()).isEqualTo(userId);
    }

    @Test
    void requireCurrentUserId_shouldThrow_whenPrincipalIsOtherType() {
        var auth = new UsernamePasswordAuthenticationToken(12345, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(facade::requireCurrentUserId)
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void requireCurrentUserId_shouldThrow_whenPrincipalIsNull() {
        var auth = new UsernamePasswordAuthenticationToken(null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(facade::requireCurrentUserId)
                .isInstanceOf(UnauthenticatedException.class);
    }

    private void setAuthentication(String principal, String... authorities) {
        var grantedAuthorities = Stream.of(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();
        var auth = new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
