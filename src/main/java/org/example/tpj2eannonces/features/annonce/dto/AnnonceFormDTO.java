package org.example.tpj2eannonces.features.annonce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Formulaire de creation/modification d'annonce")
public record AnnonceFormDTO(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 64, message = "Le titre ne doit pas depasser 64 caracteres")
        @Schema(example = "Appartement T3")
        String title,

        @NotBlank(message = "La description est obligatoire")
        @Size(max = 256, message = "La description ne doit pas depasser 256 caracteres")
        @Schema(example = "Bel appartement lumineux")
        String description,

        @NotBlank(message = "L'adresse est obligatoire")
        @Size(max = 64, message = "L'adresse ne doit pas depasser 64 caracteres")
        @Schema(example = "10 rue de Paris")
        String adress,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email doit etre valide")
        @Size(max = 64, message = "L'email ne doit pas depasser 64 caracteres")
        @Schema(example = "contact@example.com")
        String mail,

        @NotNull(message = "La categorie est obligatoire")
        @Schema(example = "1")
        Long categoryId
) {
}
