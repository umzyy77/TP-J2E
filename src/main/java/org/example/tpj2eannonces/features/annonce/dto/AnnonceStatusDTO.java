package org.example.tpj2eannonces.features.annonce.dto;

import jakarta.validation.constraints.NotBlank;

public record AnnonceStatusDTO(
        @NotBlank(message = "L'action est obligatoire")
        String action
) {
}
