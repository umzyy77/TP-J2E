package org.example.tpj2eannonces.features.auth.service;

import java.util.Set;

import org.example.tpj2eannonces.core.security.JwtService;
import org.example.tpj2eannonces.core.security.PasswordService;
import org.example.tpj2eannonces.features.auth.dto.LoginDTO;
import org.example.tpj2eannonces.features.auth.dto.LoginResponseDTO;
import org.example.tpj2eannonces.features.auth.exception.AuthUnauthorizedException;
import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Identifiants invalides";
    private static final String MISSING_ROLE_MESSAGE = "Aucun role attribue a l'utilisateur";

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordService passwordService;

    public AuthService(UserService userService, JwtService jwtService, PasswordService passwordService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
    }

    public LoginResponseDTO login(LoginDTO dto) {
        User user = userService.findWithRoleByUsername(dto.username())
                .orElseThrow(() -> new AuthUnauthorizedException(INVALID_CREDENTIALS_MESSAGE));

        if (!passwordService.matches(dto.password(), user.getPassword())) {
            throw new AuthUnauthorizedException(INVALID_CREDENTIALS_MESSAGE);
        }

        Set<String> roles = user.resolveRoleNames();
        Set<String> authorities = user.resolveAuthorities();
        if (roles.isEmpty()) {
            throw new AuthUnauthorizedException(MISSING_ROLE_MESSAGE);
        }
        String token = jwtService.generateToken(user.getId(), user.getUsername(), roles, authorities);

        long expiresIn = jwtService.getExpirationMs() / 1000;
        return new LoginResponseDTO(token, expiresIn);
    }
}
