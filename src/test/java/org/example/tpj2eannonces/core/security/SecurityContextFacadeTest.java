package org.example.tpj2eannonces.core.security;

import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.core.security.exception.UnauthenticatedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityContextFacadeTest {

    private final SecurityContextFacade securityContextFacade = new SecurityContextFacade();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRequireCurrentUserIdFromStringPrincipal() {
        UUID userId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of()));

        UUID result = securityContextFacade.requireCurrentUserId();

        assertThat(result).isEqualTo(userId);
    }

    @Test
    void shouldRequireCurrentUserIdFromUuidPrincipal() {
        UUID userId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, List.of()));

        UUID result = securityContextFacade.requireCurrentUserId();

        assertThat(result).isEqualTo(userId);
    }

    @Test
    void shouldThrowUnauthorizedWhenNoAuthentication() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> securityContextFacade.requireCurrentUserId())
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Utilisateur non authentifie");
    }

    @Test
    void shouldThrowUnauthorizedWhenPrincipalTypeIsUnsupported() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(12345L, null, List.of()));

        assertThatThrownBy(() -> securityContextFacade.requireCurrentUserId())
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Principal d'authentification invalide");
    }

    @Test
    void shouldThrowUnauthorizedWhenPrincipalIsInvalid() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("not-a-uuid", null, List.of()));

        assertThatThrownBy(() -> securityContextFacade.requireCurrentUserId())
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Principal d'authentification invalide");
    }

    @Test
    void shouldDetectAuthorityWhenPresent() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        boolean result = securityContextFacade.hasAnyAuthority("ANNONCE_ARCHIVE", "ROLE_ADMIN");

        assertThat(result).isTrue();
    }

    @Test
    void shouldIgnoreBlankGrantedAuthoritiesAndStillMatch() {
        GrantedAuthority blankAuthority = () -> " ";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user", null,
                        List.of(blankAuthority, new SimpleGrantedAuthority("ANNONCE_ARCHIVE"))));

        boolean result = securityContextFacade.hasAnyAuthority("ANNONCE_ARCHIVE", "ROLE_ADMIN");

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenAuthoritiesDoNotMatch() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        boolean result = securityContextFacade.hasAnyAuthority("ANNONCE_ARCHIVE", "ROLE_ADMIN");

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenNoAuthenticationForAuthorityCheck() {
        SecurityContextHolder.clearContext();

        boolean result = securityContextFacade.hasAnyAuthority("ROLE_ADMIN");

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenExpectedAuthoritiesAreMissing() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        assertThat(securityContextFacade.hasAnyAuthority()).isFalse();
        assertThat(securityContextFacade.hasAnyAuthority((String[]) null)).isFalse();
        assertThat(securityContextFacade.hasAnyAuthority(" ", null)).isFalse();
    }
}
