package org.example.tpj2eannonces.features.annonce.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AnnonceResponseDTO(
        Long id,
        String title,
        String description,
        String adress,
        String mail,
        LocalDateTime date,
        String status,
        AuthorDTO author,
        CategoryDTO category
) {

    public record AuthorDTO(UUID id, String username) {
    }

    public record CategoryDTO(Long id, String label) {
    }
}
