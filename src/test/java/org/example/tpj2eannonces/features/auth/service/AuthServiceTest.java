package org.example.tpj2eannonces.features.auth.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.example.tpj2eannonces.core.security.JwtService;
import org.example.tpj2eannonces.core.security.PasswordService;
import org.example.tpj2eannonces.features.auth.dto.LoginDTO;
import org.example.tpj2eannonces.features.auth.dto.LoginResponseDTO;
import org.example.tpj2eannonces.features.auth.exception.AuthUnauthorizedException;
import org.example.tpj2eannonces.features.role.model.Role;
import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginWhenCredentialsAreValid() {
        UUID userId = UUID.randomUUID();
        User user = new User("alice", "alice@example.com", "encoded-password");
        user.setId(userId);

        Role role = new Role("ROLE_ADMIN");
        role.setAuthorities(Set.of("ANNONCE_ARCHIVE"));
        user.setRole(role);

        when(userService.findWithRoleByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordService.matches("secret", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(
                eq(userId),
                eq("alice"),
                eq(Set.of("ROLE_ADMIN")),
                eq(Set.of("ROLE_ADMIN", "ANNONCE_ARCHIVE"))))
                .thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        LoginResponseDTO response = authService.login(new LoginDTO("alice", "secret"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.expiresIn()).isEqualTo(3600L);
        verify(jwtService).generateToken(
                eq(userId),
                eq("alice"),
                eq(Set.of("ROLE_ADMIN")),
                eq(Set.of("ROLE_ADMIN", "ANNONCE_ARCHIVE")));
    }

    @Test
    void shouldThrowUnauthorizedWhenUserDoesNotExist() {
        when(userService.findWithRoleByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginDTO("ghost", "pwd")))
                .isInstanceOf(AuthUnauthorizedException.class)
                .hasMessage("Identifiants invalides");

        verifyNoInteractions(passwordService, jwtService);
    }

    @Test
    void shouldThrowUnauthorizedWhenPasswordIsInvalid() {
        User user = new User("bob", "bob@example.com", "encoded-password");
        user.setId(UUID.randomUUID());

        when(userService.findWithRoleByUsername("bob")).thenReturn(Optional.of(user));
        when(passwordService.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginDTO("bob", "wrong-password")))
                .isInstanceOf(AuthUnauthorizedException.class)
                .hasMessage("Identifiants invalides");

        verify(jwtService, never()).generateToken(any(), any(), any(), any());
    }

    @Test
    void shouldThrowUnauthorizedWhenUserHasNoAssignedRole() {
        User user = new User("eve", "eve@example.com", "encoded-password");
        user.setId(UUID.randomUUID());

        when(userService.findWithRoleByUsername("eve")).thenReturn(Optional.of(user));
        when(passwordService.matches("secret", "encoded-password")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(new LoginDTO("eve", "secret")))
                .isInstanceOf(AuthUnauthorizedException.class)
                .hasMessage("Aucun role attribue a l'utilisateur");

        verify(jwtService, never()).generateToken(any(), any(), any(), any());
    }
}
