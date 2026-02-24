package org.example.tpj2eannonces.features.auth.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.tpj2eannonces.TestcontainersConfig;
import org.example.tpj2eannonces.features.annonce.repository.AnnonceRepository;
import org.example.tpj2eannonces.features.role.model.Role;
import org.example.tpj2eannonces.features.role.repository.RoleRepository;
import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class AuthControllerHardeningIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    private record RequestCase(String name, MockHttpServletRequestBuilder request) {
    }

    @BeforeEach
    void setup() {
        annonceRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = roleRepository.save(new Role("ROLE_USER"));
        buildUser(userRole);
    }

    @Test
    void loginShouldNotReturn5xxForMalformedInputs() throws Exception {
        List<RequestCase> cases = List.of(
                new RequestCase("empty body", post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")),
                new RequestCase("empty object", post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")),
                new RequestCase("null credentials", post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":null,\"password\":null}")),
                new RequestCase("wrong types", post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":123,\"password\":true}")),
                new RequestCase("malformed json", post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"owner\"")),
                new RequestCase("invalid credentials", post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"owner\",\"password\":\"bad\"}")),
                new RequestCase("missing content type", post("/api/auth/login")
                        .content("{\"username\":\"owner\",\"password\":\"password123\"}"))
        );

        assertAllNo5xx(cases);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"owner\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void randomLoginFuzzShouldNeverReturn5xx() throws Exception {
        Random random = new Random(20260223L);
        List<RequestCase> requests = new ArrayList<>();

        String[] loginBodies = {
                "",
                "{}",
                "{\"username\":\"owner\",\"password\":\"password123\"}",
                "{\"username\":\"owner\",\"password\":\"bad\"}",
                "{\"username\":null,\"password\":null}",
                "{\"username\":123,\"password\":true}",
                "{\"username\":\"owner\"",
                "{\"username\":[1,2],\"password\":{}}"
        };
        String[] contentTypes = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.TEXT_PLAIN_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                null
        };

        for (int i = 0; i < 80; i++) {
            String body = loginBodies[random.nextInt(loginBodies.length)];
            String contentType = contentTypes[random.nextInt(contentTypes.length)];
            requests.add(new RequestCase("login-fuzz-" + i, loginRequest(body, contentType)));
        }

        assertAllNo5xx(requests);
    }

    @Test
    void refreshShouldNotReturn5xxForMalformedInputs() throws Exception {
        List<RequestCase> cases = List.of(
                new RequestCase("empty body", post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")),
                new RequestCase("empty object", post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")),
                new RequestCase("null refresh token", post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":null}")),
                new RequestCase("wrong type", post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":123}")),
                new RequestCase("malformed json", post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"abc\"")),
                new RequestCase("invalid token value", post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"not-a-jwt\"}")),
                new RequestCase("missing content type", post("/api/auth/refresh")
                        .content("{\"refreshToken\":\"not-a-jwt\"}"))
        );

        assertAllNo5xx(cases);
    }

    @Test
    void randomRefreshFuzzShouldNeverReturn5xx() throws Exception {
        Random random = new Random(20260224L);
        List<RequestCase> requests = new ArrayList<>();

        String[] refreshBodies = {
                "",
                "{}",
                "{\"refreshToken\":\"not-a-jwt\"}",
                "{\"refreshToken\":null}",
                "{\"refreshToken\":123}",
                "{\"refreshToken\":\"abc\"",
                "{\"refreshToken\":[1,2,3]}"
        };
        String[] contentTypes = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.TEXT_PLAIN_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                null
        };

        for (int i = 0; i < 80; i++) {
            String body = refreshBodies[random.nextInt(refreshBodies.length)];
            String contentType = contentTypes[random.nextInt(contentTypes.length)];
            requests.add(new RequestCase("refresh-fuzz-" + i, refreshRequest(body, contentType)));
        }

        assertAllNo5xx(requests);
    }

    private MockHttpServletRequestBuilder loginRequest(String body, String contentType) {
        MockHttpServletRequestBuilder req = post("/api/auth/login").content(body);
        if (contentType != null) {
            req = req.contentType(contentType);
        }
        return req;
    }

    private MockHttpServletRequestBuilder refreshRequest(String body, String contentType) {
        MockHttpServletRequestBuilder req = post("/api/auth/refresh").content(body);
        if (contentType != null) {
            req = req.contentType(contentType);
        }
        return req;
    }

    private void buildUser(Role role) {
        User user = new User();
        user.setUsername("owner");
        user.setEmail("owner@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(role);
        userRepository.save(user);
    }

    private void assertAllNo5xx(List<RequestCase> cases) throws Exception {
        for (RequestCase requestCase : cases) {
            int status = mockMvc.perform(requestCase.request())
                    .andReturn()
                    .getResponse()
                    .getStatus();

            assertThat(status)
                    .withFailMessage("Case '%s' returned %s", requestCase.name(), status)
                    .isLessThan(500);
        }
    }
}
