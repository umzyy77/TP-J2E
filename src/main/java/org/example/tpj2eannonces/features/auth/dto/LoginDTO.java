package org.example.tpj2eannonces.features.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "Le nom d'utilisateur est requis")
        String username,

        @NotBlank(message = "Le mot de passe est requis")
        String password
) {
}
