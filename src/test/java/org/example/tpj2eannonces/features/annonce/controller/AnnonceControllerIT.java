package org.example.tpj2eannonces.features.annonce.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tpj2eannonces.TestcontainersConfig;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceFormDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceResponseDTO;
import org.example.tpj2eannonces.features.annonce.exception.AnnonceForbiddenException;
import org.example.tpj2eannonces.features.annonce.exception.AnnonceNotFoundException;
import org.example.tpj2eannonces.features.annonce.service.AnnonceService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class AnnonceControllerIT {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnnonceService annonceService;

    private static RequestPostProcessor jwt(UUID userId, String... authorities) {
        var grantedAuthorities = Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();
        return authentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null, grantedAuthorities));
    }

    // ==== 2b/2c/2d. Securite ====

    @Nested
    class Security {

        @Test
        void shouldReturn401WhenNoToken() throws Exception {
            mockMvc.perform(get("/api/annonces"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
        }

        @Test
        void shouldReturn401WhenTokenIsInvalid() throws Exception {
            mockMvc.perform(get("/api/annonces")
                            .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
        }

        @Test
        void shouldReturn403WhenRoleIsInsufficient() throws Exception {
            mockMvc.perform(get("/api/annonces")
                            .with(jwt(USER_ID, "ROLE_VISITOR")))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("FORBIDDEN"));
        }
    }

    // ==== 2e. CRUD complet ====

    // ---- GET /api/annonces ----

    @Nested
    class ListAnnonces {

        @Test
        void shouldListAllWhenNoFilters() throws Exception {
            Page<AnnonceResponseDTO> page = pageOf(response(1L));
            when(annonceService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/annonces").with(jwt(USER_ID, "ROLE_USER")))
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

            mockMvc.perform(get("/api/annonces")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .param("q", "velo"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(2));
        }
    }

    // ---- GET /api/annonces/{id} ----

    @Nested
    class GetById {

        @Test
        void shouldReturnAnnonceWhenExists() throws Exception {
            when(annonceService.findById(1L)).thenReturn(response(1L));

            mockMvc.perform(get("/api/annonces/1").with(jwt(USER_ID, "ROLE_USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Titre 1"));
        }

        @Test
        void shouldReturn404WhenNotFound() throws Exception {
            when(annonceService.findById(99L))
                    .thenThrow(new AnnonceNotFoundException("Annonce non trouvee: 99"));

            mockMvc.perform(get("/api/annonces/99").with(jwt(USER_ID, "ROLE_USER")))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("NOT_FOUND"));
        }
    }

    // ---- POST /api/annonces ----

    @Nested
    class Create {

        @Test
        void shouldCreateAndReturn201() throws Exception {
            AnnonceResponseDTO created = response(42L);
            when(annonceService.create(any(AnnonceFormDTO.class), eq(USER_ID))).thenReturn(created);

            mockMvc.perform(post("/api/annonces")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(form())))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/api/annonces/42")))
                    .andExpect(jsonPath("$.id").value(42));
        }

        @Test
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/api/annonces")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(form())))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(annonceService);
        }

        @Test
        void shouldReturn400WhenFormIsInvalid() throws Exception {
            AnnonceFormDTO invalid = new AnnonceFormDTO("", "", "", "not-email", null);

            mockMvc.perform(post("/api/annonces")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

            verifyNoInteractions(annonceService);
        }
    }

    // ---- PUT /api/annonces/{id} ----

    @Nested
    class Update {

        @Test
        void shouldUpdateWhenOwner() throws Exception {
            when(annonceService.update(eq(5L), any(AnnonceFormDTO.class), eq(USER_ID)))
                    .thenReturn(response(5L));

            mockMvc.perform(put("/api/annonces/5")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(form())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5));
        }

        @Test
        void shouldReturn403WhenNotOwner() throws Exception {
            when(annonceService.update(anyLong(), any(AnnonceFormDTO.class), eq(USER_ID)))
                    .thenThrow(new AnnonceForbiddenException("Vous n'etes pas l'auteur de cette annonce"));

            mockMvc.perform(put("/api/annonces/5")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(form())))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("FORBIDDEN"));
        }

        @Test
        void shouldReturn404WhenNotFound() throws Exception {
            when(annonceService.update(eq(99L), any(AnnonceFormDTO.class), eq(USER_ID)))
                    .thenThrow(new AnnonceNotFoundException("Annonce non trouvee: 99"));

            mockMvc.perform(put("/api/annonces/99")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(form())))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("NOT_FOUND"));
        }
    }

    // ---- DELETE /api/annonces/{id} ----

    @Nested
    class Delete {

        @Test
        void shouldDeleteAndReturn204() throws Exception {
            mockMvc.perform(delete("/api/annonces/9").with(jwt(USER_ID, "ROLE_USER")))
                    .andExpect(status().isNoContent());

            verify(annonceService).delete(9L, USER_ID);
        }

        @Test
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/api/annonces/9"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(annonceService);
        }

        @Test
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new AnnonceNotFoundException("Annonce non trouvee: 99"))
                    .when(annonceService).delete(99L, USER_ID);

            mockMvc.perform(delete("/api/annonces/99").with(jwt(USER_ID, "ROLE_USER")))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("NOT_FOUND"));
        }

        @Test
        void shouldReturn403WhenNotOwner() throws Exception {
            doThrow(new AnnonceForbiddenException("Vous n'etes pas l'auteur de cette annonce"))
                    .when(annonceService).delete(9L, USER_ID);

            mockMvc.perform(delete("/api/annonces/9").with(jwt(USER_ID, "ROLE_USER")))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("FORBIDDEN"));
        }
    }

    // ---- PATCH /api/annonces/{id} ----

    @Nested
    class ChangeStatus {

        @Test
        void shouldChangeStatusWhenValid() throws Exception {
            when(annonceService.changeStatus(7L, "publish", USER_ID)).thenReturn(response(7L));

            mockMvc.perform(patch("/api/annonces/7")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"action\":\"publish\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(7));
        }

        @Test
        void shouldReturn400WhenActionIsBlank() throws Exception {
            mockMvc.perform(patch("/api/annonces/7")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"action\":\"\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

            verifyNoInteractions(annonceService);
        }

        @Test
        void shouldReturn400WhenActionIsUnknown() throws Exception {
            when(annonceService.changeStatus(7L, "invalid", USER_ID))
                    .thenThrow(new IllegalArgumentException("Action inconnue: invalid"));

            mockMvc.perform(patch("/api/annonces/7")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"action\":\"invalid\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
        }

        @Test
        void shouldCallArchiveWhenActionIsArchive() throws Exception {
            when(annonceService.archive(7L, USER_ID)).thenReturn(response(7L));

            mockMvc.perform(patch("/api/annonces/7")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"action\":\"archive\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(7));

            verify(annonceService).archive(7L, USER_ID);
            verify(annonceService, never()).changeStatus(anyLong(), any(), any());
        }

        @Test
        void shouldReturn409WhenTransitionIsInvalid() throws Exception {
            when(annonceService.changeStatus(7L, "publish", USER_ID))
                    .thenThrow(new IllegalStateException("Transition invalide"));

            mockMvc.perform(patch("/api/annonces/7")
                            .with(jwt(USER_ID, "ROLE_USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"action\":\"publish\"}"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("CONFLICT"));
        }
    }

    // ---- Helpers ----

    private static Page<AnnonceResponseDTO> pageOf(AnnonceResponseDTO dto) {
        return new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
    }

    private static AnnonceFormDTO form() {
        return new AnnonceFormDTO("Titre annonce", "Description annonce", "10 rue de Paris", "contact@example.com", 1L);
    }

    private static AnnonceResponseDTO response(Long id) {
        return new AnnonceResponseDTO(
                id, "Titre " + id, "Description " + id, "10 rue de Paris", "contact@example.com",
                LocalDateTime.of(2026, 2, 20, 10, 30), "DRAFT",
                new AnnonceResponseDTO.AuthorDTO(USER_ID, "alice"),
                new AnnonceResponseDTO.CategoryDTO(1L, "Immobilier"));
    }
}
