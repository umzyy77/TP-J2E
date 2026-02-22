package org.example.tpj2eannonces.features.annonce.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.features.annonce.dto.AnnonceFormDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceResponseDTO;
import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.example.tpj2eannonces.features.category.model.Category;
import org.example.tpj2eannonces.features.user.model.User;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnonceMapperTest {

    private final AnnonceMapper mapper = Mappers.getMapper(AnnonceMapper.class);

    @Test
    void toEntity_shouldMapFieldsFromForm() {
        AnnonceFormDTO form = new AnnonceFormDTO("Titre", "Desc", "Adresse", "m@m.com", 1L);

        Annonce entity = mapper.toEntity(form);

        assertThat(entity.getTitle()).isEqualTo("Titre");
        assertThat(entity.getDescription()).isEqualTo("Desc");
        assertThat(entity.getAdress()).isEqualTo("Adresse");
        assertThat(entity.getMail()).isEqualTo("m@m.com");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getAuthor()).isNull();
        assertThat(entity.getCategory()).isNull();
    }

    @Test
    void toResponseDTO_shouldMapAllFields() {
        Annonce annonce = buildAnnonce();

        AnnonceResponseDTO dto = mapper.toResponseDTO(annonce);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("Titre");
        assertThat(dto.description()).isEqualTo("Desc");
        assertThat(dto.adress()).isEqualTo("Adresse");
        assertThat(dto.mail()).isEqualTo("m@m.com");
        assertThat(dto.status()).isEqualTo("DRAFT");
        assertThat(dto.author()).isNotNull();
        assertThat(dto.author().username()).isEqualTo("alice");
        assertThat(dto.category()).isNotNull();
        assertThat(dto.category().label()).isEqualTo("Immobilier");
    }

    @Test
    void toResponseDTOList_shouldMapList() {
        Annonce annonce = buildAnnonce();

        List<AnnonceResponseDTO> dtos = mapper.toResponseDTOList(List.of(annonce));

        assertThat(dtos).hasSize(1);
    }

    @Test
    void updateEntityFromDTO_shouldUpdateFields() {
        Annonce annonce = buildAnnonce();
        AnnonceFormDTO form = new AnnonceFormDTO("Nouveau", "Nouvelle desc", "Nouvelle adresse", "new@m.com", 1L);

        mapper.updateEntityFromDTO(form, annonce);

        assertThat(annonce.getTitle()).isEqualTo("Nouveau");
        assertThat(annonce.getDescription()).isEqualTo("Nouvelle desc");
        assertThat(annonce.getAdress()).isEqualTo("Nouvelle adresse");
        assertThat(annonce.getMail()).isEqualTo("new@m.com");
        // ignored fields should remain unchanged
        assertThat(annonce.getId()).isEqualTo(1L);
    }

    @Test
    void statusToString_shouldReturnName() {
        assertThat(mapper.statusToString(AnnonceStatus.DRAFT)).isEqualTo("DRAFT");
        assertThat(mapper.statusToString(AnnonceStatus.PUBLISHED)).isEqualTo("PUBLISHED");
        assertThat(mapper.statusToString(AnnonceStatus.ARCHIVED)).isEqualTo("ARCHIVED");
    }

    @Test
    void statusToString_shouldReturnNull_whenNull() {
        assertThat(mapper.statusToString(null)).isNull();
    }

    @Test
    void mapUser_shouldReturnAuthorDTO() {
        User user = new User("alice", "alice@test.com", "secret");
        UUID id = UUID.randomUUID();
        user.setId(id);

        AnnonceResponseDTO.AuthorDTO dto = mapper.map(user);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.username()).isEqualTo("alice");
    }

    @Test
    void mapUser_shouldReturnNull_whenNull() {
        assertThat(mapper.map((User) null)).isNull();
    }

    @Test
    void mapCategory_shouldReturnCategoryDTO() {
        Category category = new Category("Immobilier");
        category.setId(1L);

        AnnonceResponseDTO.CategoryDTO dto = mapper.map(category);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.label()).isEqualTo("Immobilier");
    }

    @Test
    void mapCategory_shouldReturnNull_whenNull() {
        assertThat(mapper.map((Category) null)).isNull();
    }

    private Annonce buildAnnonce() {
        Annonce annonce = new Annonce("Titre", "Desc", "Adresse", "m@m.com");
        annonce.setId(1L);
        annonce.setDate(LocalDateTime.of(2026, 1, 1, 10, 0));
        annonce.setStatus(AnnonceStatus.DRAFT);

        User author = new User("alice", "alice@test.com", "secret");
        author.setId(UUID.randomUUID());
        annonce.setAuthor(author);

        Category category = new Category("Immobilier");
        category.setId(1L);
        annonce.setCategory(category);

        return annonce;
    }
}
