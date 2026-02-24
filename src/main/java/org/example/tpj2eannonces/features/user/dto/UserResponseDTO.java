package org.example.tpj2eannonces.features.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String username,
        String email,
        String role,
        LocalDateTime createdAt
) {}
