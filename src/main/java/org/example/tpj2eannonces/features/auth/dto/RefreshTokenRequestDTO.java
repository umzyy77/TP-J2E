package org.example.tpj2eannonces.features.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank(message = "Le refresh token est requis")
        String refreshToken
) {
}
