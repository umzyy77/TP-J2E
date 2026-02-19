package org.example.tpj2eannonces.api.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.api.dto.annonce.AnnonceCreateDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceResponseDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceSearchParams;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceStatusDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceUpdateDTO;
import org.example.tpj2eannonces.api.dto.common.PaginatedResponseDTO;
import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.example.tpj2eannonces.exception.ForbiddenException;
import org.example.tpj2eannonces.exception.NotFoundException;
import org.example.tpj2eannonces.exception.annonce.AnnonceImmutableException;
import org.example.tpj2eannonces.exception.annonce.ArchiveRequiredException;
import org.example.tpj2eannonces.exception.annonce.InvalidTransitionException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.service.AnnonceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@ExtendWith(MockitoExtension.class)
class AnnonceResourceTest {

    @Mock
    private AnnonceService annonceService;

    @Mock
    private AnnonceSearchParams params;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UriInfo uriInfo;

    private AnnonceResource resource;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        resource = new AnnonceResource(annonceService);
    }

    private Annonce createTestAnnonce(Long id, String title) {
        Annonce annonce = new Annonce();
        annonce.setId(id);
        annonce.setTitle(title);
        annonce.setDescription("Description");
        annonce.setAdress("Adresse");
        annonce.setMail("mail@test.com");
        annonce.setStatus(AnnonceStatus.DRAFT);
        User author = new User("testuser", "test@test.com", "pass");
        author.setId(userId);
        annonce.setAuthor(author);
        Category category = new Category("Immobilier");
        category.setId(1L);
        annonce.setCategory(category);
        return annonce;
    }

    // --- Tests pour list() ---

    @Nested
    class ListTests {

        @Test
        void list_shouldReturn200WithPaginatedResponse_noFilters() {
            when(params.hasKeyword()).thenReturn(false);
            when(params.hasFilters()).thenReturn(false);
            when(params.getPage()).thenReturn(0);
            when(params.getSize()).thenReturn(10);
            when(annonceService.findAll(0, 10)).thenReturn(List.of());
            when(annonceService.count()).thenReturn(0L);

            Response response = resource.list(params);

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getEntity()).isInstanceOf(PaginatedResponseDTO.class);
            verify(annonceService).findAll(0, 10);
            verify(annonceService).count();
        }

        @Test
        void list_shouldDelegateToSearchByFilters_whenKeywordPresent() {
            when(params.hasKeyword()).thenReturn(true);
            when(params.getKeyword()).thenReturn("voiture");
            when(params.getCategoryId()).thenReturn(null);
            when(params.getStatus()).thenReturn(null);
            when(params.getPage()).thenReturn(0);
            when(params.getSize()).thenReturn(10);
            when(annonceService.searchByFilters("voiture", null, null, 0, 10)).thenReturn(List.of());
            when(annonceService.countBySearchAndFilters("voiture", null, null)).thenReturn(0L);

            Response response = resource.list(params);

            assertThat(response.getStatus()).isEqualTo(200);
            verify(annonceService).searchByFilters("voiture", null, null, 0, 10);
            verify(annonceService).countBySearchAndFilters("voiture", null, null);
        }

        @Test
        void list_shouldDelegateToFindByFilters_whenFiltersPresent() {
            when(params.hasKeyword()).thenReturn(false);
            when(params.hasFilters()).thenReturn(true);
            when(params.getCategoryId()).thenReturn(1L);
            when(params.getStatus()).thenReturn(AnnonceStatus.DRAFT);
            when(params.getPage()).thenReturn(0);
            when(params.getSize()).thenReturn(5);
            when(annonceService.findByFilters(1L, AnnonceStatus.DRAFT, 0, 5)).thenReturn(List.of());
            when(annonceService.countByFilters(1L, AnnonceStatus.DRAFT)).thenReturn(0L);

            Response response = resource.list(params);

            assertThat(response.getStatus()).isEqualTo(200);
            verify(annonceService).findByFilters(1L, AnnonceStatus.DRAFT, 0, 5);
            verify(annonceService).countByFilters(1L, AnnonceStatus.DRAFT);
        }

        @Test
        void list_shouldReturnCorrectPaginationMetadata() {
            Annonce annonce = createTestAnnonce(1L, "Test");
            when(params.hasKeyword()).thenReturn(false);
            when(params.hasFilters()).thenReturn(false);
            when(params.getPage()).thenReturn(0);
            when(params.getSize()).thenReturn(10);
            when(annonceService.findAll(0, 10)).thenReturn(List.of(annonce));
            when(annonceService.count()).thenReturn(1L);

            Response response = resource.list(params);

            @SuppressWarnings("unchecked")
            PaginatedResponseDTO<AnnonceResponseDTO> body =
                    (PaginatedResponseDTO<AnnonceResponseDTO>) response.getEntity();
            assertThat(body.data()).hasSize(1);
            assertThat(body.totalCount()).isEqualTo(1L);
            assertThat(body.page()).isZero();
            assertThat(body.pageSize()).isEqualTo(10);
        }

        @Test
        void list_shouldMapAnnoncesToResponseDTOs() {
            Annonce annonce = createTestAnnonce(1L, "Mon Annonce");
            when(params.hasKeyword()).thenReturn(false);
            when(params.hasFilters()).thenReturn(false);
            when(params.getPage()).thenReturn(0);
            when(params.getSize()).thenReturn(10);
            when(annonceService.findAll(0, 10)).thenReturn(List.of(annonce));
            when(annonceService.count()).thenReturn(1L);

            Response response = resource.list(params);

            @SuppressWarnings("unchecked")
            PaginatedResponseDTO<AnnonceResponseDTO> body =
                    (PaginatedResponseDTO<AnnonceResponseDTO>) response.getEntity();
            AnnonceResponseDTO dto = body.data().getFirst();
            assertThat(dto.title()).isEqualTo("Mon Annonce");
            assertThat(dto.status()).isEqualTo("DRAFT");
            assertThat(dto.author()).isNotNull();
            assertThat(dto.category()).isNotNull();
        }
    }

    // --- Tests pour getById() ---

    @Nested
    class GetByIdTests {

        @Test
        void getById_shouldReturn200WithAnnonce() {
            Annonce annonce = createTestAnnonce(1L, "Test");
            when(annonceService.findByIdWithRelations(1L)).thenReturn(Optional.of(annonce));

            Response response = resource.getById(1L);

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getEntity()).isInstanceOf(AnnonceResponseDTO.class);
            AnnonceResponseDTO dto = (AnnonceResponseDTO) response.getEntity();
            assertThat(dto.title()).isEqualTo("Test");
        }

        @Test
        void getById_shouldThrowNotFoundException_whenNotFound() {
            when(annonceService.findByIdWithRelations(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resource.getById(99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // --- Tests pour create() ---

    @Nested
    class CreateTests {

        @BeforeEach
        void setUpSecurity() throws Exception {
            UserPrincipal principal = new UserPrincipal(userId, "testuser");
            when(securityContext.getUserPrincipal()).thenReturn(principal);
            // Inject securityContext via reflection
            var field = AnnonceResource.class.getDeclaredField("securityContext");
            field.setAccessible(true);
            field.set(resource, securityContext);
        }

        @Test
        void create_shouldReturn201WithLocationHeader() {
            Annonce created = createTestAnnonce(42L, "Titre");
            when(annonceService.create(any(Annonce.class), eq(userId), eq(1L))).thenReturn(created);
            when(annonceService.findByIdWithRelations(42L)).thenReturn(Optional.of(created));
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri("http://localhost/api/annonces"));

            AnnonceCreateDTO dto = new AnnonceCreateDTO("Titre", "Desc", "Adresse", "mail@test.com", 1L);

            Response response = resource.create(dto, uriInfo);

            assertThat(response.getStatus()).isEqualTo(201);
            assertThat(response.getLocation()).isNotNull();
            assertThat(response.getLocation().toString()).contains("42");
            assertThat(response.getEntity()).isInstanceOf(AnnonceResponseDTO.class);
        }

        @Test
        void create_shouldMapDTOFieldsToEntity() {
            Annonce created = createTestAnnonce(1L, "Titre");
            when(annonceService.create(any(Annonce.class), eq(userId), eq(5L))).thenReturn(created);
            when(annonceService.findByIdWithRelations(1L)).thenReturn(Optional.of(created));
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri("http://localhost/api/annonces"));

            AnnonceCreateDTO dto = new AnnonceCreateDTO("Titre", "Desc", "Adresse", "mail@test.com", 5L);

            resource.create(dto, uriInfo);

            verify(annonceService).create(any(Annonce.class), eq(userId), eq(5L));
        }
    }

    // --- Tests pour update() ---

    @Nested
    class UpdateTests {

        @BeforeEach
        void setUpSecurity() throws Exception {
            UserPrincipal principal = new UserPrincipal(userId, "testuser");
            when(securityContext.getUserPrincipal()).thenReturn(principal);
            var field = AnnonceResource.class.getDeclaredField("securityContext");
            field.setAccessible(true);
            field.set(resource, securityContext);
        }

        @Test
        void update_shouldReturn200WithUpdatedAnnonce() {
            Annonce updated = createTestAnnonce(1L, "New Title");
            when(annonceService.updateFields(eq(1L), eq(userId), anyString(), anyString(), anyString(), anyString(), anyLong()))
                    .thenReturn(updated);
            when(annonceService.findByIdWithRelations(1L)).thenReturn(Optional.of(updated));

            AnnonceUpdateDTO dto = new AnnonceUpdateDTO("New Title", "Desc", "Addr", "m@t.com", 1L);
            Response response = resource.update(1L, dto);

            assertThat(response.getStatus()).isEqualTo(200);
            AnnonceResponseDTO body = (AnnonceResponseDTO) response.getEntity();
            assertThat(body.title()).isEqualTo("New Title");
        }

        @Test
        void update_shouldPropagateNotFoundException() {
            when(annonceService.updateFields(eq(99L), eq(userId), anyString(), anyString(), anyString(), anyString(), anyLong()))
                    .thenThrow(new NotFoundException("Annonce non trouvee: 99"));

            AnnonceUpdateDTO dto = new AnnonceUpdateDTO("T", "D", "A", "m@t.com", 1L);

            assertThatThrownBy(() -> resource.update(99L, dto))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        void update_shouldPropagateAnnonceImmutableException() {
            when(annonceService.updateFields(eq(1L), eq(userId), anyString(), anyString(), anyString(), anyString(), anyLong()))
                    .thenThrow(new AnnonceImmutableException());

            AnnonceUpdateDTO dto = new AnnonceUpdateDTO("T", "D", "A", "m@t.com", 1L);

            assertThatThrownBy(() -> resource.update(1L, dto))
                    .isInstanceOf(AnnonceImmutableException.class);
        }

        @Test
        void update_shouldPropagateForbiddenException() {
            when(annonceService.updateFields(eq(1L), eq(userId), anyString(), anyString(), anyString(), anyString(), anyLong()))
                    .thenThrow(new ForbiddenException("Vous n'etes pas l'auteur"));

            AnnonceUpdateDTO dto = new AnnonceUpdateDTO("T", "D", "A", "m@t.com", 1L);

            assertThatThrownBy(() -> resource.update(1L, dto))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    // --- Tests pour delete() ---

    @Nested
    class DeleteTests {

        @BeforeEach
        void setUpSecurity() throws Exception {
            UserPrincipal principal = new UserPrincipal(userId, "testuser");
            when(securityContext.getUserPrincipal()).thenReturn(principal);
            var field = AnnonceResource.class.getDeclaredField("securityContext");
            field.setAccessible(true);
            field.set(resource, securityContext);
        }

        @Test
        void delete_shouldReturn204() {
            when(annonceService.delete(1L, userId)).thenReturn(true);

            Response response = resource.delete(1L);

            assertThat(response.getStatus()).isEqualTo(204);
            verify(annonceService).delete(1L, userId);
        }

        @Test
        void delete_shouldPropagateArchiveRequiredException() {
            when(annonceService.delete(1L, userId)).thenThrow(new ArchiveRequiredException());

            assertThatThrownBy(() -> resource.delete(1L))
                    .isInstanceOf(ArchiveRequiredException.class);
        }

        @Test
        void delete_shouldPropagateNotFoundException() {
            when(annonceService.delete(99L, userId)).thenThrow(new NotFoundException("Annonce non trouvee: 99"));

            assertThatThrownBy(() -> resource.delete(99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // --- Tests pour changeStatus() ---

    @Nested
    class ChangeStatusTests {

        @BeforeEach
        void setUpSecurity() throws Exception {
            UserPrincipal principal = new UserPrincipal(userId, "testuser");
            when(securityContext.getUserPrincipal()).thenReturn(principal);
            var field = AnnonceResource.class.getDeclaredField("securityContext");
            field.setAccessible(true);
            field.set(resource, securityContext);
        }

        @Test
        void changeStatus_shouldReturn200WithUpdatedStatus() {
            Annonce published = createTestAnnonce(1L, "Test");
            published.setStatus(AnnonceStatus.PUBLISHED);
            when(annonceService.changeStatus(1L, userId, "publish")).thenReturn(published);
            when(annonceService.findByIdWithRelations(1L)).thenReturn(Optional.of(published));

            Response response = resource.changeStatus(1L, new AnnonceStatusDTO("publish"));

            assertThat(response.getStatus()).isEqualTo(200);
            AnnonceResponseDTO body = (AnnonceResponseDTO) response.getEntity();
            assertThat(body.status()).isEqualTo("PUBLISHED");
        }

        @Test
        void changeStatus_shouldPropagateInvalidTransitionException() {
            when(annonceService.changeStatus(1L, userId, "archive"))
                    .thenThrow(new InvalidTransitionException("archive"));

            AnnonceStatusDTO request = new AnnonceStatusDTO("archive");
            assertThatThrownBy(() -> resource.changeStatus(1L, request))
                    .isInstanceOf(InvalidTransitionException.class);
        }

        @Test
        void changeStatus_shouldPropagateNotFoundException() {
            when(annonceService.changeStatus(99L, userId, "publish"))
                    .thenThrow(new NotFoundException("Annonce non trouvee: 99"));

            AnnonceStatusDTO request = new AnnonceStatusDTO("publish");
            assertThatThrownBy(() -> resource.changeStatus(99L, request))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
