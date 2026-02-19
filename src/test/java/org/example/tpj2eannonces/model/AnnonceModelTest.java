package org.example.tpj2eannonces.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class AnnonceModelTest {

    @Test
    void onCreate_shouldSetDateWhenNull() {
        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        assertThat(annonce.getDate()).isNull();

        annonce.onCreate();

        assertThat(annonce.getDate()).isNotNull();
    }

    @Test
    void onCreate_shouldKeepExistingDate() {
        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        LocalDateTime existingDate = LocalDateTime.of(2026, 1, 1, 10, 30);
        annonce.setDate(existingDate);

        annonce.onCreate();

        assertThat(annonce.getDate()).isEqualTo(existingDate);
    }

    @Test
    void getOwnerId_shouldReturnNullWhenAuthorIsNull() {
        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        assertThat(annonce.getOwnerId()).isNull();
    }

    @Test
    void getDateAsDate_shouldHandleNullAndNonNullDate() {
        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        assertThat(annonce.getDateAsDate()).isNull();

        LocalDateTime now = LocalDateTime.now();
        annonce.setDate(now);
        Date asDate = annonce.getDateAsDate();

        assertThat(asDate).isNotNull();
        assertThat(asDate.toInstant().toEpochMilli()).isGreaterThan(0);
    }

    @Test
    void toString_shouldContainMainFields() {
        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        annonce.setId(10L);
        String text = annonce.toString();

        assertThat(text)
                .contains("id=10")
                .contains("Titre")
                .contains("Addr");
    }

    @Test
    void equals_shouldHandleReflexiveNullAndOtherType() {
        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        assertThat(annonce).isEqualTo(annonce);
        assertThat(annonce.equals(null)).isFalse();
        assertThat(annonce.equals("not-annonce")).isFalse();
    }

    @Test
    void equalsAndHashCode_shouldUseId() {
        Annonce a1 = new Annonce("T1", "D", "A", "m1@test.com");
        Annonce a2 = new Annonce("T2", "D", "A", "m2@test.com");
        a1.setId(1L);
        a2.setId(1L);

        assertThat(a1)
                .isEqualTo(a2)
                .hasSameHashCodeAs(a2);

        User author = new User("u", "u@test.com", "p");
        UUID id = UUID.randomUUID();
        author.setId(id);
        a1.setAuthor(author);
        assertThat(a1.getOwnerId()).isEqualTo(id);
    }
}
