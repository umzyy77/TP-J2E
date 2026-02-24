package org.example.tpj2eannonces.features.annonce.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.example.tpj2eannonces.TestcontainersConfig;
import org.example.tpj2eannonces.core.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class AnnonceMetaControllerHardeningIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String validToken;

    private record RequestCase(String name, MockHttpServletRequestBuilder request) {
    }

    @BeforeEach
    void setup() {
        validToken = jwtService.generateToken(
                UUID.randomUUID(),
                "meta-user",
                Set.of("ROLE_USER"),
                Set.of("ROLE_USER"));
    }

    @Test
    void metaEndpointShouldNotReturn5xx() throws Exception {
        List<RequestCase> cases = List.of(
                new RequestCase("meta without token", get("/api/meta/annonces")),
                new RequestCase("meta with valid token", withBearer(get("/api/meta/annonces"), validToken)),
                new RequestCase("meta with corrupted token", withBearer(get("/api/meta/annonces"), "bad.token.value"))
        );

        assertAllNo5xx(cases);
    }

    @Test
    void randomMetaFuzzShouldNeverReturn5xx() throws Exception {
        Random random = new Random(20260223L);
        List<RequestCase> requests = new ArrayList<>();
        String[] maybeTokens = {validToken, "bad.token.value", null};
        String[] maybeSort = {"title,asc", "unknownField,asc", "title;drop table annonce,asc", null};
        String[] maybePage = {"0", "-1", "abc", null};
        String[] maybeSize = {"10", "-1", "abc", null};

        for (int i = 0; i < 50; i++) {
            MockHttpServletRequestBuilder req = get("/api/meta/annonces");
            String token = maybeTokens[random.nextInt(maybeTokens.length)];
            String sort = maybeSort[random.nextInt(maybeSort.length)];
            String page = maybePage[random.nextInt(maybePage.length)];
            String size = maybeSize[random.nextInt(maybeSize.length)];

            req = maybeParam(req, "sort", sort);
            req = maybeParam(req, "page", page);
            req = maybeParam(req, "size", size);
            req = withMaybeBearer(req, token);

            requests.add(new RequestCase("meta-fuzz-" + i, req));
        }

        assertAllNo5xx(requests);
    }

    private MockHttpServletRequestBuilder withBearer(MockHttpServletRequestBuilder request, String token) {
        return request.header("Authorization", "Bearer " + token);
    }

    private MockHttpServletRequestBuilder withMaybeBearer(MockHttpServletRequestBuilder request, String token) {
        if (token == null) {
            return request;
        }
        return request.header("Authorization", "Bearer " + token);
    }

    private MockHttpServletRequestBuilder maybeParam(MockHttpServletRequestBuilder request, String key, String value) {
        if (value == null) {
            return request;
        }
        return request.param(key, value);
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
