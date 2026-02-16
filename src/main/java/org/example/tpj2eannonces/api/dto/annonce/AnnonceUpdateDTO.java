package org.example.tpj2eannonces.api.dto.annonce;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnnonceUpdateDTO(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 64, message = "Le titre ne doit pas depasser 64 caracteres")
        String title,

        @NotBlank(message = "La description est obligatoire")
        @Size(max = 256, message = "La description ne doit pas depasser 256 caracteres")
        String description,

        @NotBlank(message = "L'adresse est obligatoire")
        @Size(max = 64, message = "L'adresse ne doit pas depasser 64 caracteres")
        String adress,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email doit etre valide")
        @Size(max = 64, message = "L'email ne doit pas depasser 64 caracteres")
        String mail,

        @NotNull(message = "La categorie est obligatoire")
        Long categoryId
) {
}
