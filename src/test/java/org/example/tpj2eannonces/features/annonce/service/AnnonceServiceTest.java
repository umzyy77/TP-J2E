package org.example.tpj2eannonces.features.annonce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.features.annonce.dto.AnnonceFormDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceResponseDTO;
import org.example.tpj2eannonces.features.annonce.exception.AnnonceForbiddenException;
import org.example.tpj2eannonces.features.annonce.exception.AnnonceNotFoundException;
import org.example.tpj2eannonces.features.annonce.mapper.AnnonceMapper;
import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.example.tpj2eannonces.features.annonce.repository.AnnonceRepository;
import org.example.tpj2eannonces.features.category.model.Category;
import org.example.tpj2eannonces.features.category.repository.CategoryRepository;
import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private AnnonceRepository annonceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AnnonceMapper annonceMapper;

    @InjectMocks
    private AnnonceService annonceService;

    // ---- findById ----

    @Test
    void findById_shouldReturnDto_whenExists() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        AnnonceResponseDTO dto = responseDto(1L);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
        when(annonceMapper.toResponseDTO(annonce)).thenReturn(dto);

        AnnonceResponseDTO result = annonceService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(annonceRepository.findWithRelationsById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> annonceService.findById(99L))
                .isInstanceOf(AnnonceNotFoundException.class);
    }

    // ---- findAll ----

    @Test
    void findAll_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        Page<Annonce> page = new PageImpl<>(List.of(annonce), pageable, 1);
        when(annonceRepository.findAll(pageable)).thenReturn(page);
        when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDto(1L));

        Page<AnnonceResponseDTO> result = annonceService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    // ---- create ----

    @Test
    void create_shouldSaveAndReturnDto() {
        AnnonceFormDTO form = form();
        User user = new User();
        user.setId(OWNER_ID);
        Category category = new Category();
        category.setId(1L);
        Annonce entity = annonce(1L, AnnonceStatus.DRAFT);

        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(annonceMapper.toEntity(form)).thenReturn(entity);
        when(annonceRepository.save(entity)).thenReturn(entity);
        when(annonceMapper.toResponseDTO(entity)).thenReturn(responseDto(1L));

        AnnonceResponseDTO result = annonceService.create(form, OWNER_ID);

        assertThat(result.id()).isEqualTo(1L);
        verify(annonceRepository).save(entity);
    }

    // ---- update ----

    @Test
    void update_shouldThrow_whenNotOwner() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.update(1L, form(), OTHER_ID))
                .isInstanceOf(AnnonceForbiddenException.class);
    }

    @Test
    void update_shouldThrow_whenPublished() {
        Annonce annonce = annonce(1L, AnnonceStatus.PUBLISHED);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.update(1L, form(), OWNER_ID))
                .isInstanceOf(AnnonceForbiddenException.class);
    }

    // ---- delete ----

    @Test
    void delete_shouldThrow_whenNotArchived() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.delete(1L, OWNER_ID))
                .isInstanceOf(AnnonceForbiddenException.class);
    }

    @Test
    void delete_shouldSucceed_whenArchivedAndOwner() {
        Annonce annonce = annonce(1L, AnnonceStatus.ARCHIVED);
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

        annonceService.delete(1L, OWNER_ID);

        verify(annonceRepository).delete(annonce);
    }

    // ---- update success ----

    @Test
    void update_shouldSucceed_whenDraftAndOwner_sameCategory() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        AnnonceFormDTO form = form();
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
        when(annonceRepository.save(annonce)).thenReturn(annonce);
        when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDto(1L));

        AnnonceResponseDTO result = annonceService.update(1L, form, OWNER_ID);

        assertThat(result.id()).isEqualTo(1L);
        verify(annonceMapper).updateEntityFromDTO(form, annonce);
    }

    @Test
    void update_shouldChangeCategory_whenDifferentCategoryId() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        AnnonceFormDTO form = new AnnonceFormDTO("Titre", "Desc", "Adresse", "m@m.com", 99L);
        Category newCat = new Category();
        newCat.setId(99L);

        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
        when(categoryRepository.findById(99L)).thenReturn(Optional.of(newCat));
        when(annonceRepository.save(annonce)).thenReturn(annonce);
        when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDto(1L));

        annonceService.update(1L, form, OWNER_ID);

        assertThat(annonce.getCategory()).isEqualTo(newCat);
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        when(annonceRepository.findWithRelationsById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> annonceService.update(99L, form(), OWNER_ID))
                .isInstanceOf(AnnonceNotFoundException.class);
    }

    @Test
    void update_shouldThrow_whenNewCategoryNotFound() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        AnnonceFormDTO form = new AnnonceFormDTO("Titre", "Desc", "Adresse", "m@m.com", 99L);

        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> annonceService.update(1L, form, OWNER_ID))
                .isInstanceOf(AnnonceNotFoundException.class);
    }

    // ---- search ----

    @SuppressWarnings("unchecked")
    @Test
    void search_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        Page<Annonce> page = new PageImpl<>(List.of(annonce), pageable, 1);
        when(annonceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDto(1L));

        Page<AnnonceResponseDTO> result = annonceService.search(
                "keyword", AnnonceStatus.DRAFT, 1L, OWNER_ID,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    // ---- changeStatus ----

    @Test
    void changeStatus_shouldThrow_whenActionUnknown() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.changeStatus(1L, "invalid", OWNER_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changeStatus_shouldSucceed_whenValidTransition() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
        when(annonceRepository.save(annonce)).thenReturn(annonce);
        when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDto(1L));

        AnnonceResponseDTO result = annonceService.changeStatus(1L, "publish", OWNER_ID);

        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
        assertThat(result).isNotNull();
    }

    @Test
    void changeStatus_shouldThrow_whenInvalidTransition() {
        Annonce annonce = annonce(1L, AnnonceStatus.PUBLISHED);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.changeStatus(1L, "publish", OWNER_ID))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void changeStatus_shouldThrow_whenNotOwner() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.changeStatus(1L, "publish", OTHER_ID))
                .isInstanceOf(AnnonceForbiddenException.class);
    }

    @Test
    void changeStatus_shouldThrow_whenNotFound() {
        when(annonceRepository.findWithRelationsById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> annonceService.changeStatus(99L, "publish", OWNER_ID))
                .isInstanceOf(AnnonceNotFoundException.class);
    }

    // ---- archive ----

    @Test
    void archive_shouldSucceed_whenPublishedAndOwner() {
        Annonce annonce = annonce(1L, AnnonceStatus.PUBLISHED);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
        when(annonceRepository.save(annonce)).thenReturn(annonce);
        when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDto(1L));

        AnnonceResponseDTO result = annonceService.archive(1L, OWNER_ID);

        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.ARCHIVED);
        assertThat(result).isNotNull();
    }

    @Test
    void archive_shouldThrow_whenNotPublished() {
        Annonce annonce = annonce(1L, AnnonceStatus.DRAFT);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.archive(1L, OWNER_ID))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void archive_shouldThrow_whenNotOwner() {
        Annonce annonce = annonce(1L, AnnonceStatus.PUBLISHED);
        when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.archive(1L, OTHER_ID))
                .isInstanceOf(AnnonceForbiddenException.class);
    }

    // ---- create error paths ----

    @Test
    void create_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> annonceService.create(form(), OWNER_ID))
                .isInstanceOf(AnnonceNotFoundException.class);
    }

    @Test
    void create_shouldThrow_whenCategoryNotFound() {
        User user = new User();
        user.setId(OWNER_ID);
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> annonceService.create(form(), OWNER_ID))
                .isInstanceOf(AnnonceNotFoundException.class);
    }

    // ---- delete error paths ----

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(annonceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> annonceService.delete(99L, OWNER_ID))
                .isInstanceOf(AnnonceNotFoundException.class);
    }

    @Test
    void delete_shouldThrow_whenNotOwner() {
        Annonce annonce = annonce(1L, AnnonceStatus.ARCHIVED);
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.delete(1L, OTHER_ID))
                .isInstanceOf(AnnonceForbiddenException.class);
    }

    @Test
    void delete_shouldThrow_whenNullUserId() {
        Annonce annonce = annonce(1L, AnnonceStatus.ARCHIVED);
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

        assertThatThrownBy(() -> annonceService.delete(1L, null))
                .isInstanceOf(AnnonceForbiddenException.class);
    }

    // ---- Helpers ----

    private Annonce annonce(Long id, AnnonceStatus status) {
        Annonce a = new Annonce();
        a.setId(id);
        a.setTitle("Titre " + id);
        a.setDescription("Desc");
        a.setAdress("Adresse");
        a.setMail("test@test.com");
        a.setDate(LocalDateTime.now());
        a.setStatus(status);

        User owner = new User();
        owner.setId(OWNER_ID);
        a.setAuthor(owner);

        Category cat = new Category();
        cat.setId(1L);
        a.setCategory(cat);

        return a;
    }

    private static AnnonceFormDTO form() {
        return new AnnonceFormDTO("Titre", "Description", "10 rue de Paris", "contact@example.com", 1L);
    }

    private static AnnonceResponseDTO responseDto(Long id) {
        return new AnnonceResponseDTO(
                id, "Titre " + id, "Desc", "Adresse", "test@test.com",
                LocalDateTime.of(2026, 2, 20, 10, 30), "DRAFT",
                new AnnonceResponseDTO.AuthorDTO(OWNER_ID, "alice"),
                new AnnonceResponseDTO.CategoryDTO(1L, "Immobilier"));
    }
}
