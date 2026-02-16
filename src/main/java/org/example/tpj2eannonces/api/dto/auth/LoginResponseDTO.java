package org.example.tpj2eannonces.api.dto.auth;

public record LoginResponseDTO(String token, long expiresIn) {
}
