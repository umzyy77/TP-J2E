package org.example.tpj2eannonces.features.annonce.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.tpj2eannonces.features.category.model.Category;
import org.example.tpj2eannonces.features.user.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnonceTest {

    @Test
    void defaultConstructor_shouldSetDraftStatus() {
        Annonce annonce = new Annonce();
        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.DRAFT);
    }

    @Test
    void parameterizedConstructor_shouldSetFieldsAndDraftStatus() {
        Annonce annonce = new Annonce("Titre", "Desc", "Adresse", "mail@test.com");

        assertThat(annonce.getTitle()).isEqualTo("Titre");
        assertThat(annonce.getDescription()).isEqualTo("Desc");
        assertThat(annonce.getAdress()).isEqualTo("Adresse");
        assertThat(annonce.getMail()).isEqualTo("mail@test.com");
        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.DRAFT);
    }

    @Test
    void gettersAndSetters_shouldWork() {
        Annonce annonce = new Annonce();
        LocalDateTime now = LocalDateTime.now();
        User author = new User();
        Category category = new Category();

        annonce.setId(1L);
        annonce.setTitle("T");
        annonce.setDescription("D");
        annonce.setAdress("A");
        annonce.setMail("m@m.com");
        annonce.setDate(now);
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        annonce.setVersion(2L);
        annonce.setAuthor(author);
        annonce.setCategory(category);

        assertThat(annonce.getId()).isEqualTo(1L);
        assertThat(annonce.getTitle()).isEqualTo("T");
        assertThat(annonce.getDescription()).isEqualTo("D");
        assertThat(annonce.getAdress()).isEqualTo("A");
        assertThat(annonce.getMail()).isEqualTo("m@m.com");
        assertThat(annonce.getDate()).isEqualTo(now);
        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
        assertThat(annonce.getVersion()).isEqualTo(2L);
        assertThat(annonce.getAuthor()).isEqualTo(author);
        assertThat(annonce.getCategory()).isEqualTo(category);
    }

    @Test
    void getOwnerId_shouldReturnAuthorId() {
        Annonce annonce = new Annonce();
        User author = new User();
        UUID userId = UUID.randomUUID();
        author.setId(userId);
        annonce.setAuthor(author);

        assertThat(annonce.getOwnerId()).isEqualTo(userId);
    }

    @Test
    void getOwnerId_shouldReturnNull_whenNoAuthor() {
        Annonce annonce = new Annonce();
        assertThat(annonce.getOwnerId()).isNull();
    }

    @Test
    void onCreate_shouldSetDate_whenNull() {
        Annonce annonce = new Annonce();
        annonce.onCreate();
        assertThat(annonce.getDate()).isNotNull();
    }

    @Test
    void onCreate_shouldNotOverrideDate_whenAlreadySet() {
        Annonce annonce = new Annonce();
        LocalDateTime existing = LocalDateTime.of(2020, 1, 1, 0, 0);
        annonce.setDate(existing);
        annonce.onCreate();
        assertThat(annonce.getDate()).isEqualTo(existing);
    }

    @Test
    void onUpdate_shouldRefreshDate() {
        Annonce annonce = new Annonce();
        annonce.setDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        annonce.onUpdate();
        assertThat(annonce.getDate()).isAfter(LocalDateTime.of(2020, 1, 1, 0, 0));
    }

    @Test
    void equals_shouldBeTrue_whenSameId() {
        Annonce a1 = new Annonce();
        a1.setId(1L);
        Annonce a2 = new Annonce();
        a2.setId(1L);

        assertThat(a1).isEqualTo(a2);
    }

    @Test
    void equals_shouldBeFalse_whenDifferentId() {
        Annonce a1 = new Annonce();
        a1.setId(1L);
        Annonce a2 = new Annonce();
        a2.setId(2L);

        assertThat(a1).isNotEqualTo(a2);
    }

    @Test
    void equals_shouldBeFalse_whenComparedToNull() {
        Annonce a1 = new Annonce();
        a1.setId(1L);
        assertThat(a1).isNotEqualTo(null);
    }

    @Test
    void equals_shouldBeFalse_whenComparedToDifferentType() {
        Annonce a1 = new Annonce();
        a1.setId(1L);
        assertThat(a1).isNotEqualTo("not an annonce");
    }

    @Test
    void equals_shouldBeTrue_whenSameInstance() {
        Annonce a1 = new Annonce();
        a1.setId(1L);
        assertThat(a1).isEqualTo(a1);
    }

    @Test
    void hashCode_shouldBeEqual_whenSameId() {
        Annonce a1 = new Annonce();
        a1.setId(1L);
        Annonce a2 = new Annonce();
        a2.setId(1L);

        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        Annonce annonce = new Annonce();
        annonce.setId(1L);
        annonce.setTitle("Mon Titre");
        annonce.setStatus(AnnonceStatus.DRAFT);

        String result = annonce.toString();
        assertThat(result).contains("id=1");
        assertThat(result).contains("Mon Titre");
        assertThat(result).contains("DRAFT");
    }
}
