package org.example.tpj2eannonces.api.dto.annonce;

import jakarta.validation.constraints.NotBlank;

public record AnnonceStatusDTO(
        @NotBlank(message = "L'action est obligatoire")
        String action
) {
}
