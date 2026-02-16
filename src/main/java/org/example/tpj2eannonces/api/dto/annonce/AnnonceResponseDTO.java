package org.example.tpj2eannonces.api.dto.annonce;

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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String title;
        private String description;
        private String adress;
        private String mail;
        private LocalDateTime date;
        private String status;
        private AuthorDTO author;
        private CategoryDTO category;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder adress(String adress) {
            this.adress = adress;
            return this;
        }

        public Builder mail(String mail) {
            this.mail = mail;
            return this;
        }

        public Builder date(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder author(AuthorDTO author) {
            this.author = author;
            return this;
        }

        public Builder category(CategoryDTO category) {
            this.category = category;
            return this;
        }

        public AnnonceResponseDTO build() {
            return new AnnonceResponseDTO(id, title, description, adress, mail, date, status, author, category);
        }
    }
}
