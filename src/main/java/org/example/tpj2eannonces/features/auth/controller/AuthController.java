package org.example.tpj2eannonces.features.auth.controller;

import org.example.tpj2eannonces.core.web.dto.ApiErrorDTO;
import org.example.tpj2eannonces.features.auth.dto.LoginDTO;
import org.example.tpj2eannonces.features.auth.dto.LoginResponseDTO;
import org.example.tpj2eannonces.features.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Endpoints d'authentification")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponse(responseCode = "200", description = "Connexion reussie",
            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class),
                    examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzI1NiJ9...\", \"expiresIn\": 3600000}")))
    @ApiResponse(responseCode = "401", description = "Identifiants invalides",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class),
                    examples = @ExampleObject(value = "{\"error\": \"UNAUTHORIZED\", \"messages\": [\"Identifiants invalides\"]}")))
    @ApiResponse(responseCode = "400", description = "Donnees invalides",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class),
                    examples = @ExampleObject(
                            value = "{\"error\": \"VALIDATION_ERROR\", \"messages\": [\"username: Le nom d'utilisateur est requis\"]}")))
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
