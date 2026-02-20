package org.example.tpj2eannonces.features.annonce.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.features.annonce.dto.AnnonceFormDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceResponseDTO;
import org.example.tpj2eannonces.features.annonce.mapper.AnnonceMapper;
import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.example.tpj2eannonces.features.annonce.repository.AnnonceRepository;
import org.example.tpj2eannonces.features.annonce.service.AnnonceService;
import org.example.tpj2eannonces.features.category.model.Category;
import org.example.tpj2eannonces.features.category.repository.CategoryRepository;
import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.repository.UserRepository;
import org.example.tpj2eannonces.shared.exception.ForbiddenException;
import org.example.tpj2eannonces.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

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

    private UUID userId;
    private UUID otherUserId;
    private User user;
    private Category category;
    private Annonce annonce;
    private AnnonceResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        user = new User("testuser", "test@test.com", "hashedpw");
        user.setId(userId);

        category = new Category("Immobilier");
        category.setId(1L);

        annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");
        annonce.setId(1L);
        annonce.setAuthor(user);
        annonce.setCategory(category);
        annonce.setDate(LocalDateTime.now());

        responseDTO = new AnnonceResponseDTO(
                1L, "Titre", "Description", "Adresse", "mail@test.com",
                LocalDateTime.now(), "DRAFT",
                new AnnonceResponseDTO.AuthorDTO(userId, "testuser"),
                new AnnonceResponseDTO.CategoryDTO(1L, "Immobilier")
        );
    }

    @Nested
    class FindById {

        @Test
        void shouldReturnAnnonceWhenFound() {
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
            when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDTO);

            AnnonceResponseDTO result = annonceService.findById(1L);

            assertThat(result).isEqualTo(responseDTO);
        }

        @Test
        void shouldThrowNotFoundWhenAnnonceDoesNotExist() {
            when(annonceRepository.findWithRelationsById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> annonceService.findById(99L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    class FindAll {

        @Test
        void shouldReturnPaginatedResults() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Annonce> page = new PageImpl<>(List.of(annonce), pageable, 1);
            when(annonceRepository.findAll(pageable)).thenReturn(page);
            when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDTO);

            Page<AnnonceResponseDTO> result = annonceService.findAll(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    class Search {

        @Test
        @SuppressWarnings("unchecked")
        void shouldSearchWithFilters() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Annonce> page = new PageImpl<>(List.of(annonce), pageable, 1);
            when(annonceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDTO);

            Page<AnnonceResponseDTO> result = annonceService.search(
                    "voiture", AnnonceStatus.DRAFT, 1L, null, null, null, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    class Create {

        @Test
        void shouldCreateAnnonce() {
            AnnonceFormDTO formDTO = new AnnonceFormDTO("Titre", "Desc", "Adresse", "m@t.com", 1L);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(annonceMapper.toEntity(formDTO)).thenReturn(annonce);
            when(annonceRepository.save(annonce)).thenReturn(annonce);
            when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDTO);

            AnnonceResponseDTO result = annonceService.create(formDTO, userId);

            assertThat(result).isEqualTo(responseDTO);
            verify(annonceRepository).save(annonce);
        }

        @Test
        void shouldThrowNotFoundWhenUserDoesNotExist() {
            AnnonceFormDTO formDTO = new AnnonceFormDTO("Titre", "Desc", "Adresse", "m@t.com", 1L);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> annonceService.create(formDTO, userId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Utilisateur");
        }

        @Test
        void shouldThrowNotFoundWhenCategoryDoesNotExist() {
            AnnonceFormDTO formDTO = new AnnonceFormDTO("Titre", "Desc", "Adresse", "m@t.com", 99L);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> annonceService.create(formDTO, userId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Categorie");
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateAnnonceWhenOwner() {
            annonce.setStatus(AnnonceStatus.DRAFT);
            AnnonceFormDTO formDTO = new AnnonceFormDTO("New", "NewDesc", "NewAddr", "n@t.com", 1L);
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
            when(annonceRepository.save(annonce)).thenReturn(annonce);
            when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDTO);

            AnnonceResponseDTO result = annonceService.update(1L, formDTO, userId);

            assertThat(result).isEqualTo(responseDTO);
            verify(annonceMapper).updateEntityFromDTO(formDTO, annonce);
        }

        @Test
        void shouldThrowForbiddenWhenNotOwner() {
            annonce.setStatus(AnnonceStatus.DRAFT);
            AnnonceFormDTO formDTO = new AnnonceFormDTO("New", "NewDesc", "NewAddr", "n@t.com", 1L);
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

            assertThatThrownBy(() -> annonceService.update(1L, formDTO, otherUserId))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessageContaining("auteur");
        }

        @Test
        void shouldThrowForbiddenWhenPublished() {
            annonce.setStatus(AnnonceStatus.PUBLISHED);
            AnnonceFormDTO formDTO = new AnnonceFormDTO("New", "NewDesc", "NewAddr", "n@t.com", 1L);
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

            assertThatThrownBy(() -> annonceService.update(1L, formDTO, userId))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessageContaining("publiee");
        }

        @Test
        void shouldThrowNotFoundWhenAnnonceDoesNotExist() {
            AnnonceFormDTO formDTO = new AnnonceFormDTO("New", "NewDesc", "NewAddr", "n@t.com", 1L);
            when(annonceRepository.findWithRelationsById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> annonceService.update(99L, formDTO, userId))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteArchivedAnnonceWhenOwner() {
            annonce.setStatus(AnnonceStatus.ARCHIVED);
            when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

            annonceService.delete(1L, userId);

            verify(annonceRepository).delete(annonce);
        }

        @Test
        void shouldThrowForbiddenWhenNotOwner() {
            annonce.setStatus(AnnonceStatus.ARCHIVED);
            when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

            assertThatThrownBy(() -> annonceService.delete(1L, otherUserId))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        void shouldThrowForbiddenWhenNotArchived() {
            annonce.setStatus(AnnonceStatus.DRAFT);
            when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

            assertThatThrownBy(() -> annonceService.delete(1L, userId))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessageContaining("archivee");
        }

        @Test
        void shouldThrowNotFoundWhenAnnonceDoesNotExist() {
            when(annonceRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> annonceService.delete(99L, userId))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    class ChangeStatus {

        @BeforeEach
        void setUpSecurityContext() {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            userId.toString(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        }

        @Test
        void shouldPublishDraftAnnonce() {
            annonce.setStatus(AnnonceStatus.DRAFT);
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
            when(annonceRepository.save(annonce)).thenReturn(annonce);
            when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDTO);

            annonceService.changeStatus(1L, "publish", userId);

            assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
            verify(annonceRepository).save(annonce);
        }

        @Test
        void shouldArchivePublishedAnnonceWhenAdmin() {
            annonce.setStatus(AnnonceStatus.PUBLISHED);
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));
            when(annonceRepository.save(annonce)).thenReturn(annonce);
            when(annonceMapper.toResponseDTO(annonce)).thenReturn(responseDTO);

            annonceService.changeStatus(1L, "archive", userId);

            assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.ARCHIVED);
        }

        @Test
        void shouldThrowForbiddenWhenNonAdminTriesToArchive() {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            userId.toString(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))));

            annonce.setStatus(AnnonceStatus.PUBLISHED);
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

            assertThatThrownBy(() -> annonceService.changeStatus(1L, "archive", userId))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessageContaining("administrateur");
        }

        @Test
        void shouldThrowIllegalArgumentWhenActionUnknown() {
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

            assertThatThrownBy(() -> annonceService.changeStatus(1L, "invalid", userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("inconnue");
        }

        @Test
        void shouldThrowIllegalStateWhenWrongCurrentStatus() {
            annonce.setStatus(AnnonceStatus.ARCHIVED);
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

            assertThatThrownBy(() -> annonceService.changeStatus(1L, "publish", userId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Transition invalide");
        }

        @Test
        void shouldThrowForbiddenWhenNotOwner() {
            annonce.setStatus(AnnonceStatus.DRAFT);
            when(annonceRepository.findWithRelationsById(1L)).thenReturn(Optional.of(annonce));

            assertThatThrownBy(() -> annonceService.changeStatus(1L, "publish", otherUserId))
                    .isInstanceOf(ForbiddenException.class);
        }
    }
}
