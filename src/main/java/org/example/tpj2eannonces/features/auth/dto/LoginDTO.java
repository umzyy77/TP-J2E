package org.example.tpj2eannonces.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Identifiants de connexion")
public record LoginDTO(
        @NotBlank(message = "Le nom d'utilisateur est requis")
        @Schema(example = "alice")
        String username,

        @NotBlank(message = "Le mot de passe est requis")
        @Schema(example = "secret123")
        String password
) {
}
