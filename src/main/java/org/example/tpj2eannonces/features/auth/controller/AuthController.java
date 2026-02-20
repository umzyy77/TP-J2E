package org.example.tpj2eannonces.features.auth.controller;

import org.example.tpj2eannonces.core.security.JwtService;
import org.example.tpj2eannonces.features.auth.dto.LoginDTO;
import org.example.tpj2eannonces.features.auth.dto.LoginResponseDTO;
import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.repository.UserRepository;
import org.example.tpj2eannonces.shared.dto.ApiErrorDTO;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        User user = userRepository.findByUsername(dto.username()).orElse(null);

        if (user == null || !BCrypt.checkpw(dto.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiErrorDTO.of("UNAUTHORIZED", "Identifiants invalides"));
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        long expiresIn = jwtService.getExpirationMs() / 1000;

        return ResponseEntity.ok(new LoginResponseDTO(token, expiresIn));
    }
}
