package org.example.tpj2eannonces.features.annonce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Action de changement de statut")
public record AnnonceStatusDTO(
        @NotBlank(message = "L'action est obligatoire")
        @Schema(example = "publish", description = "Action: publish ou archive")
        String action
) {
}
