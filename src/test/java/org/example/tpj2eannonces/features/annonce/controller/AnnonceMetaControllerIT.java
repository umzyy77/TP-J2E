package org.example.tpj2eannonces.features.annonce.controller;

import java.util.Arrays;
import java.util.UUID;

import org.example.tpj2eannonces.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class AnnonceMetaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static RequestPostProcessor jwt() {
        var authorities = Arrays.stream(new String[]{"ROLE_USER"})
                .map(SimpleGrantedAuthority::new)
                .toList();
        return authentication(
                new UsernamePasswordAuthenticationToken(USER_ID.toString(), null, authorities));
    }

    @Test
    void shouldReturnMetadata() throws Exception {
        mockMvc.perform(get("/api/meta/annonces").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortableFields").isArray())
                .andExpect(jsonPath("$.filterableFields").isArray())
                .andExpect(jsonPath("$.searchableFields").isArray());
    }

    @Test
    void shouldReturn401WhenNoAuth() throws Exception {
        mockMvc.perform(get("/api/meta/annonces"))
                .andExpect(status().isUnauthorized());
    }
}
