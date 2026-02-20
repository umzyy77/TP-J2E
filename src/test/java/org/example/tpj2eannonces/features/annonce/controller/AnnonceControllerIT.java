package org.example.tpj2eannonces.features.annonce.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tpj2eannonces.core.security.SecurityContextFacade;
import org.example.tpj2eannonces.core.security.exception.UnauthenticatedException;
import org.example.tpj2eannonces.core.web.exception.GlobalExceptionHandler;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceFormDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceResponseDTO;
import org.example.tpj2eannonces.features.annonce.exception.AnnonceForbiddenException;
import org.example.tpj2eannonces.features.annonce.exception.AnnonceNotFoundException;
import org.example.tpj2eannonces.features.annonce.service.AnnonceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnnonceControllerIT {

    private static final UUID CURRENT_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID AUTHOR_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private AnnonceService annonceService;

    @Mock
    private SecurityContextFacade securityContextFacade;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AnnonceController controller = new AnnonceController(annonceService, securityContextFacade);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldListAllWhenNoFilters() throws Exception {
        Page<AnnonceResponseDTO> page = pageOf(response(1L));
        when(annonceService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/annonces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Titre 1"));

        verify(annonceService).findAll(any(Pageable.class));
        verify(annonceService, never()).search(any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void shouldSearchWhenFiltersAreProvided() throws Exception {
        Page<AnnonceResponseDTO> page = pageOf(response(2L));
        when(annonceService.search(eq("velo"), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/annonces").param("q", "velo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Titre 2"));

        verify(annonceService).search(eq("velo"), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void shouldCreateAnnonceWhenAuthenticated() throws Exception {
        AnnonceFormDTO request = form();
        AnnonceResponseDTO created = response(42L);
        when(securityContextFacade.requireCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(annonceService.create(any(AnnonceFormDTO.class), eq(CURRENT_USER_ID))).thenReturn(created);

        mockMvc.perform(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/annonces/42")))
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void shouldReturnUnauthorizedOnCreateWhenNoAuthenticatedUser() throws Exception {
        when(securityContextFacade.requireCurrentUserId())
                .thenThrow(new UnauthenticatedException("Utilisateur non authentifie"));

        mockMvc.perform(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.messages[0]").value("Utilisateur non authentifie"));

        verifyNoInteractions(annonceService);
    }

    @Test
    void shouldReturnNotFoundWhenAnnonceDoesNotExist() throws Exception {
        when(annonceService.findById(99L))
                .thenThrow(new AnnonceNotFoundException("Annonce non trouvee: 99"));

        mockMvc.perform(get("/api/annonces/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void shouldReturnForbiddenWhenUpdateIsRejected() throws Exception {
        when(securityContextFacade.requireCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(annonceService.update(anyLong(), any(AnnonceFormDTO.class), eq(CURRENT_USER_ID)))
                .thenThrow(new AnnonceForbiddenException("Vous n'etes pas l'auteur de cette annonce"));

        mockMvc.perform(put("/api/annonces/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void shouldReturnBadRequestWhenStatusActionIsBlank() throws Exception {
        mockMvc.perform(patch("/api/annonces/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"action":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

        verifyNoInteractions(securityContextFacade, annonceService);
    }

    @Test
    void shouldReturnBadRequestWhenStatusActionIsInvalid() throws Exception {
        when(securityContextFacade.requireCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(annonceService.changeStatus(7L, "invalid", CURRENT_USER_ID))
                .thenThrow(new IllegalArgumentException("Action inconnue: invalid"));

        mockMvc.perform(patch("/api/annonces/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"action":"invalid"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnConflictWhenStatusTransitionIsInvalid() throws Exception {
        when(securityContextFacade.requireCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(annonceService.changeStatus(7L, "publish", CURRENT_USER_ID))
                .thenThrow(new IllegalStateException("Transition invalide"));

        mockMvc.perform(patch("/api/annonces/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"action":"publish"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test
    void shouldDeleteAnnonceWhenAuthenticated() throws Exception {
        when(securityContextFacade.requireCurrentUserId()).thenReturn(CURRENT_USER_ID);

        mockMvc.perform(delete("/api/annonces/9"))
                .andExpect(status().isNoContent());

        verify(annonceService).delete(9L, CURRENT_USER_ID);
    }

    private static Page<AnnonceResponseDTO> pageOf(AnnonceResponseDTO responseDTO) {
        return new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 10), 1);
    }

    private static AnnonceFormDTO form() {
        return new AnnonceFormDTO(
                "Titre annonce",
                "Description annonce",
                "10 rue de Paris",
                "contact@example.com",
                1L
        );
    }

    private static AnnonceResponseDTO response(Long id) {
        return new AnnonceResponseDTO(
                id,
                "Titre " + id,
                "Description " + id,
                "10 rue de Paris",
                "contact@example.com",
                LocalDateTime.of(2026, 2, 20, 10, 30),
                "DRAFT",
                new AnnonceResponseDTO.AuthorDTO(AUTHOR_ID, "alice"),
                new AnnonceResponseDTO.CategoryDTO(1L, "Immobilier")
        );
    }
}
