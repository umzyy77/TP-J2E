package org.example.tpj2eannonces.features.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tpj2eannonces.TestcontainersConfig;
import org.example.tpj2eannonces.features.auth.dto.LoginDTO;
import org.example.tpj2eannonces.features.auth.dto.LoginResponseDTO;
import org.example.tpj2eannonces.features.auth.exception.AuthUnauthorizedException;
import org.example.tpj2eannonces.features.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class AuthControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    // ---- 2a. login ----

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginDTO request = new LoginDTO("alice", "secret");
        when(authService.login(request)).thenReturn(new LoginResponseDTO("jwt-token", 3600L));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600));

        verify(authService).login(request);
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        when(authService.login(any(LoginDTO.class)))
                .thenThrow(new AuthUnauthorizedException("Identifiants invalides"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDTO("alice", "bad"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDTO(" ", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

        verifyNoInteractions(authService);
    }

    @Test
    void shouldBeAccessibleWithoutAuthentication() throws Exception {
        when(authService.login(any(LoginDTO.class)))
                .thenReturn(new LoginResponseDTO("token", 3600L));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDTO("user", "pass"))))
                .andExpect(status().isOk());
    }
}
