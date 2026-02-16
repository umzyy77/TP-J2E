package org.example.tpj2eannonces.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.api.dto.annonce.AnnonceCreateDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceResponseDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceUpdateDTO;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;
import org.junit.jupiter.api.Test;

class AnnonceMapperTest {

    @Test
    void toEntity_shouldMapDtoToAnnonce() {
        AnnonceCreateDTO dto = new AnnonceCreateDTO(
                UUID.randomUUID(), "Titre", "Description", "Adresse", "mail@test.com", 1L);

        Annonce annonce = AnnonceMapper.toEntity(dto);

        assertThat(annonce.getTitle()).isEqualTo("Titre");
        assertThat(annonce.getDescription()).isEqualTo("Description");
        assertThat(annonce.getAdress()).isEqualTo("Adresse");
        assertThat(annonce.getMail()).isEqualTo("mail@test.com");
    }

    @Test
    void updateEntity_shouldUpdateAnnonceFields() {
        Annonce annonce = new Annonce("Old", "Old desc", "Old addr", "old@test.com");
        AnnonceUpdateDTO dto = new AnnonceUpdateDTO("New", "New desc", "New addr", "new@test.com", 1L);

        AnnonceMapper.updateEntity(annonce, dto);

        assertThat(annonce.getTitle()).isEqualTo("New");
        assertThat(annonce.getDescription()).isEqualTo("New desc");
        assertThat(annonce.getAdress()).isEqualTo("New addr");
        assertThat(annonce.getMail()).isEqualTo("new@test.com");
    }

    @Test
    void toResponseDTO_shouldMapAnnonceWithRelations() {
        User author = new User("john", "john@test.com", "pass");
        Category category = new Category("Immobilier");

        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        annonce.setStatus(AnnonceStatus.DRAFT);
        annonce.setAuthor(author);
        annonce.setCategory(category);

        AnnonceResponseDTO dto = AnnonceMapper.toResponseDTO(annonce);

        assertThat(dto.title()).isEqualTo("Titre");
        assertThat(dto.description()).isEqualTo("Desc");
        assertThat(dto.status()).isEqualTo("DRAFT");
        assertThat(dto.author()).isNotNull();
        assertThat(dto.author().username()).isEqualTo("john");
        assertThat(dto.category()).isNotNull();
        assertThat(dto.category().label()).isEqualTo("Immobilier");
    }

    @Test
    void toResponseDTO_shouldHandleNullRelations() {
        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        annonce.setStatus(AnnonceStatus.DRAFT);

        AnnonceResponseDTO dto = AnnonceMapper.toResponseDTO(annonce);

        assertThat(dto.title()).isEqualTo("Titre");
        assertThat(dto.author()).isNull();
        assertThat(dto.category()).isNull();
    }

    @Test
    void toResponseDTO_shouldHandleNullStatus() {
        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        annonce.setStatus(null);

        AnnonceResponseDTO dto = AnnonceMapper.toResponseDTO(annonce);

        assertThat(dto.status()).isNull();
    }

    @Test
    void toResponseDTOList_shouldMapList() {
        Annonce a1 = new Annonce("T1", "D1", "A1", "m1@test.com");
        a1.setStatus(AnnonceStatus.DRAFT);
        Annonce a2 = new Annonce("T2", "D2", "A2", "m2@test.com");
        a2.setStatus(AnnonceStatus.PUBLISHED);

        List<AnnonceResponseDTO> dtos = AnnonceMapper.toResponseDTOList(List.of(a1, a2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).title()).isEqualTo("T1");
        assertThat(dtos.get(1).title()).isEqualTo("T2");
    }
}
