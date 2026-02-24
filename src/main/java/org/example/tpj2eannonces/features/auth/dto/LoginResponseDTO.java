package org.example.tpj2eannonces.features.auth.dto;

public record LoginResponseDTO(String token,
                               long expiresIn,
                               String refreshToken,
                               long refreshExpiresIn) {

    public LoginResponseDTO(String token, long expiresIn) {
        this(token, expiresIn, null, 0L);
    }
}
