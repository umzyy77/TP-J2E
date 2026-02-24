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
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    void login_shouldReturnToken_whenCredentialsAreValid() {
        User user = userWithRole();
        when(userService.findWithRoleByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordService.matches("secret", user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any(UUID.class), eq("alice"), anyCollection(), anyCollection()))
                .thenReturn("jwt-access-token");
        when(jwtService.generateRefreshToken(any(UUID.class), eq("alice")))
                .thenReturn("jwt-refresh-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);
        when(jwtService.getRefreshExpirationMs()).thenReturn(604800000L);

        LoginResponseDTO result = authService.login(new LoginDTO("alice", "secret"));

        assertThat(result.token()).isEqualTo("jwt-access-token");
        assertThat(result.expiresIn()).isEqualTo(3600L);
        assertThat(result.refreshToken()).isEqualTo("jwt-refresh-token");
        assertThat(result.refreshExpiresIn()).isEqualTo(604800L);
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        LoginDTO loginDTO = new LoginDTO("unknown", "pass");
        when(userService.findWithRoleByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginDTO))
                .isInstanceOf(AuthUnauthorizedException.class);
    }

    @Test
    void login_shouldThrow_whenPasswordIsWrong() {
        LoginDTO loginDTO = new LoginDTO("alice", "bad");
        User user = userWithRole();
        when(userService.findWithRoleByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordService.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginDTO))
                .isInstanceOf(AuthUnauthorizedException.class);
    }

    @Test
    void login_shouldThrow_whenUserHasNoRole() {
        LoginDTO loginDTO = new LoginDTO("alice", "secret");
        User user = userWithoutRole();
        when(userService.findWithRoleByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordService.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(loginDTO))
                .isInstanceOf(AuthUnauthorizedException.class);
    }

    @Test
    void refresh_shouldReturnNewTokens_whenRefreshTokenIsValid() {
        User user = userWithRole();
        String refreshToken = "refresh-token";

        when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUserIdFromRefreshToken(refreshToken)).thenReturn(user.getId());
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UUID.class), eq("alice"), anyCollection(), anyCollection()))
                .thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(any(UUID.class), eq("alice")))
                .thenReturn("new-refresh-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);
        when(jwtService.getRefreshExpirationMs()).thenReturn(604800000L);

        LoginResponseDTO result = authService.refresh(refreshToken);

        assertThat(result.token()).isEqualTo("new-access-token");
        assertThat(result.expiresIn()).isEqualTo(3600L);
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.refreshExpiresIn()).isEqualTo(604800L);
    }

    @Test
    void refresh_shouldThrow_whenRefreshTokenIsInvalid() {
        when(jwtService.validateRefreshToken("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh("bad-token"))
                .isInstanceOf(AuthUnauthorizedException.class);
    }

    @Test
    void refresh_shouldThrow_whenExtractUserIdThrowsIllegalArgument() {
        when(jwtService.validateRefreshToken("bad-uuid-token")).thenReturn(true);
        when(jwtService.extractUserIdFromRefreshToken("bad-uuid-token"))
                .thenThrow(new IllegalArgumentException("Invalid UUID"));

        assertThatThrownBy(() -> authService.refresh("bad-uuid-token"))
                .isInstanceOf(AuthUnauthorizedException.class);
    }

    @Test
    void refresh_shouldThrow_whenUserHasNoRole() {
        User user = userWithoutRole();
        String refreshToken = "refresh-token";

        when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUserIdFromRefreshToken(refreshToken)).thenReturn(user.getId());
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.refresh(refreshToken))
                .isInstanceOf(AuthUnauthorizedException.class);
    }

    @Test
    void refresh_shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(jwtService.validateRefreshToken("refresh-token")).thenReturn(true);
        when(jwtService.extractUserIdFromRefreshToken("refresh-token")).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("refresh-token"))
                .isInstanceOf(AuthUnauthorizedException.class);
    }

    private User userWithRole() {
        Role role = new Role("ROLE_USER");
        role.setId(1L);
        role.setAuthorities(Set.of("ANNONCE_READ", "ANNONCE_WRITE"));

        User user = new User("alice", "alice@test.com", "encoded-password");
        user.setId(UUID.randomUUID());
        user.setRole(role);
        return user;
    }

    private User userWithoutRole() {
        Role role = new Role("");
        role.setId(1L);

        User user = new User("alice", "alice@test.com", "encoded-password");
        user.setId(UUID.randomUUID());
        user.setRole(role);
        return user;
    }
}
