package org.example.tpj2eannonces.features.annonce.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.example.tpj2eannonces.TestcontainersConfig;
import org.example.tpj2eannonces.core.security.JwtService;
import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.example.tpj2eannonces.features.annonce.repository.AnnonceRepository;
import org.example.tpj2eannonces.features.category.model.Category;
import org.example.tpj2eannonces.features.category.repository.CategoryRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class AnnonceControllerHardeningIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private String ownerToken;
    private String otherToken;
    private String adminToken;

    private User owner;
    private User otherUser;
    private Category category;

    private Annonce draftAnnonce;
    private Annonce publishedAnnonce;
    private Annonce archivedAnnonce;
    private Annonce otherArchivedAnnonce;

    private record RequestCase(String name, MockHttpServletRequestBuilder request) {
    }

    @BeforeEach
    void setup() {
        annonceRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        categoryRepository.deleteAll();

        Role userRole = new Role("ROLE_USER");
        userRole = roleRepository.save(userRole);

        Role adminRole = new Role("ROLE_ADMIN");
        adminRole.setAuthorities(Set.of("ANNONCE_ARCHIVE"));
        adminRole = roleRepository.save(adminRole);

        category = categoryRepository.save(new Category("Immobilier"));

        owner = buildUser("owner", "owner@example.com", userRole);
        otherUser = buildUser("other", "other@example.com", userRole);
        User admin = buildUser("admin", "admin@example.com", adminRole);

        ownerToken = tokenFor(owner);
        otherToken = tokenFor(otherUser);
        adminToken = tokenFor(admin);

        draftAnnonce = saveAnnonce(owner, category, AnnonceStatus.DRAFT, "Draft title");
        publishedAnnonce = saveAnnonce(owner, category, AnnonceStatus.PUBLISHED, "Published title");
        archivedAnnonce = saveAnnonce(owner, category, AnnonceStatus.ARCHIVED, "Archived title");
        otherArchivedAnnonce = saveAnnonce(otherUser, category, AnnonceStatus.ARCHIVED, "Other archived");
    }

    @Test
    void listEndpointShouldNotReturn5xxForQueryFuzzing() throws Exception {
        List<RequestCase> cases = new ArrayList<>();
        cases.add(new RequestCase("no filters", withBearer(get("/api/annonces"), ownerToken)));
        cases.add(new RequestCase("valid filters", withBearer(get("/api/annonces")
                .param("q", "title")
                .param("status", "DRAFT")
                .param("categoryId", String.valueOf(category.getId())), ownerToken)));
        cases.add(new RequestCase("invalid status", withBearer(get("/api/annonces")
                .param("status", "INVALID"), ownerToken)));
        cases.add(new RequestCase("invalid category id type", withBearer(get("/api/annonces")
                .param("categoryId", "abc"), ownerToken)));
        cases.add(new RequestCase("invalid author uuid", withBearer(get("/api/annonces")
                .param("authorId", "not-a-uuid"), ownerToken)));
        cases.add(new RequestCase("invalid fromDate", withBearer(get("/api/annonces")
                .param("fromDate", "2026-99-99T99:99:99"), ownerToken)));
        cases.add(new RequestCase("invalid toDate", withBearer(get("/api/annonces")
                .param("toDate", "not-a-date"), ownerToken)));
        cases.add(new RequestCase("negative size", withBearer(get("/api/annonces")
                .param("size", "-1"), ownerToken)));
        cases.add(new RequestCase("negative page", withBearer(get("/api/annonces")
                .param("page", "-1"), ownerToken)));
        cases.add(new RequestCase("unknown sort property", withBearer(get("/api/annonces")
                .param("sort", "unknownField,asc"), ownerToken)));
        cases.add(new RequestCase("sql-like sort payload", withBearer(get("/api/annonces")
                .param("sort", "title;drop table annonce,asc"), ownerToken)));
        cases.add(new RequestCase("long q filter", withBearer(get("/api/annonces")
                .param("q", "x".repeat(4000)), ownerToken)));
        cases.add(new RequestCase("without token", get("/api/annonces")));
        cases.add(new RequestCase("corrupted token", withBearer(get("/api/annonces"), "bad.token.value")));

        assertAllNo5xx(cases);
    }

    @Test
    void getByIdEndpointShouldNotReturn5xxForPathFuzzing() throws Exception {
        List<RequestCase> cases = List.of(
                new RequestCase("existing id", withBearer(get("/api/annonces/" + draftAnnonce.getId()), ownerToken)),
                new RequestCase("missing id", withBearer(get("/api/annonces/999999999"), ownerToken)),
                new RequestCase("zero id", withBearer(get("/api/annonces/0"), ownerToken)),
                new RequestCase("negative id", withBearer(get("/api/annonces/-1"), ownerToken)),
                new RequestCase("non numeric id", withBearer(get("/api/annonces/abc"), ownerToken)),
                new RequestCase("overflow id", withBearer(get("/api/annonces/999999999999999999999999"), ownerToken)),
                new RequestCase("without token", get("/api/annonces/" + draftAnnonce.getId()))
        );

        assertAllNo5xx(cases);
    }

    @Test
    void createEndpointShouldNotReturn5xxForBodyFuzzing() throws Exception {
        String validBody = """
                {"title":"Titre valide","description":"Description valide","adress":"10 rue de Paris","mail":"ok@example.com","categoryId":%d}
                """.formatted(category.getId());

        List<RequestCase> cases = List.of(
                new RequestCase("valid create", withBearer(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody), ownerToken)),
                new RequestCase("missing body", withBearer(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON), ownerToken)),
                new RequestCase("empty object", withBearer(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"), ownerToken)),
                new RequestCase("invalid email", withBearer(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Titre","description":"Desc","adress":"Adr","mail":"bad-mail","categoryId":%d}
                                """.formatted(category.getId())), ownerToken)),
                new RequestCase("unknown category", withBearer(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Titre","description":"Desc","adress":"Adr","mail":"ok@example.com","categoryId":999999}
                                """), ownerToken)),
                new RequestCase("overlong title", withBearer(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"%s","description":"Desc","adress":"Adr","mail":"ok@example.com","categoryId":%d}
                                """.formatted("x".repeat(65), category.getId())), ownerToken)),
                new RequestCase("wrong field types", withBearer(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":123,"description":true,"adress":[],"mail":{},"categoryId":"abc"}
                                """), ownerToken)),
                new RequestCase("malformed json", withBearer(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"A\""), ownerToken)),
                new RequestCase("without token", post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
        );

        assertAllNo5xx(cases);
    }

    @Test
    void updateEndpointShouldNotReturn5xxForFuzzing() throws Exception {
        String validBody = """
                {"title":"Nouveau titre","description":"Nouvelle description","adress":"11 rue de Paris","mail":"new@example.com","categoryId":%d}
                """.formatted(category.getId());

        List<RequestCase> cases = List.of(
                new RequestCase("owner valid update", withBearer(put("/api/annonces/" + draftAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody), ownerToken)),
                new RequestCase("not owner", withBearer(put("/api/annonces/" + draftAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody), otherToken)),
                new RequestCase("update published", withBearer(put("/api/annonces/" + publishedAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody), ownerToken)),
                new RequestCase("unknown id", withBearer(put("/api/annonces/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody), ownerToken)),
                new RequestCase("invalid path id", withBearer(put("/api/annonces/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody), ownerToken)),
                new RequestCase("missing body", withBearer(put("/api/annonces/" + draftAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON), ownerToken)),
                new RequestCase("malformed json", withBearer(put("/api/annonces/" + draftAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"A\""), ownerToken)),
                new RequestCase("unknown category", withBearer(put("/api/annonces/" + draftAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Titre","description":"Desc","adress":"Adr","mail":"ok@example.com","categoryId":999999}
                                """), ownerToken)),
                new RequestCase("without token", put("/api/annonces/" + draftAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
        );

        assertAllNo5xx(cases);
    }

    @Test
    void patchEndpointShouldNotReturn5xxForFuzzing() throws Exception {
        List<RequestCase> cases = List.of(
                new RequestCase("publish draft valid", withBearer(patch("/api/annonces/" + draftAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"publish\"}"), ownerToken)),
                new RequestCase("publish already published", withBearer(patch("/api/annonces/" + publishedAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"publish\"}"), ownerToken)),
                new RequestCase("archive with non-admin", withBearer(patch("/api/annonces/" + publishedAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"archive\"}"), ownerToken)),
                new RequestCase("archive with admin", withBearer(patch("/api/annonces/" + publishedAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"archive\"}"), adminToken)),
                new RequestCase("invalid action", withBearer(patch("/api/annonces/" + archivedAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"invalid\"}"), ownerToken)),
                new RequestCase("blank action", withBearer(patch("/api/annonces/" + archivedAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"\"}"), ownerToken)),
                new RequestCase("missing action", withBearer(patch("/api/annonces/" + archivedAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"), ownerToken)),
                new RequestCase("malformed json", withBearer(patch("/api/annonces/" + archivedAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\""), ownerToken)),
                new RequestCase("invalid path id", withBearer(patch("/api/annonces/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"publish\"}"), ownerToken)),
                new RequestCase("unknown id", withBearer(patch("/api/annonces/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"publish\"}"), ownerToken)),
                new RequestCase("without token", patch("/api/annonces/" + archivedAnnonce.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"publish\"}"))
        );

        assertAllNo5xx(cases);
    }

    @Test
    void deleteEndpointShouldNotReturn5xxForFuzzing() throws Exception {
        Annonce deletable = saveAnnonce(owner, category, AnnonceStatus.ARCHIVED, "Deletable");

        List<RequestCase> cases = List.of(
                new RequestCase("delete archived owner", withBearer(delete("/api/annonces/" + deletable.getId()), ownerToken)),
                new RequestCase("delete non archived", withBearer(delete("/api/annonces/" + draftAnnonce.getId()), ownerToken)),
                new RequestCase("delete not owner", withBearer(delete("/api/annonces/" + otherArchivedAnnonce.getId()), ownerToken)),
                new RequestCase("delete unknown id", withBearer(delete("/api/annonces/999999"), ownerToken)),
                new RequestCase("delete invalid path", withBearer(delete("/api/annonces/abc"), ownerToken)),
                new RequestCase("delete without token", delete("/api/annonces/" + archivedAnnonce.getId()))
        );

        assertAllNo5xx(cases);
    }

    @Test
    void randomAnnonceFuzzShouldNeverReturn5xx() throws Exception {
        Random random = new Random(20260223L);
        List<RequestCase> requests = new ArrayList<>();

        String[] maybeTokens = {ownerToken, adminToken, "bad.token.value", null};
        String[] idCandidates = {
                String.valueOf(draftAnnonce.getId()),
                String.valueOf(publishedAnnonce.getId()),
                String.valueOf(archivedAnnonce.getId()),
                "0",
                "-1",
                "999999999",
                "abc",
                "9999999999999999999999"
        };
        String[] statuses = {"DRAFT", "PUBLISHED", "ARCHIVED", "INVALID", "", null};
        String[] categoryIds = {String.valueOf(category.getId()), "abc", "-1", "999999999", null};
        String[] authorIds = {UUID.randomUUID().toString(), "not-a-uuid", "", null};
        String[] dates = {"2026-02-23T10:15:30", "not-a-date", "2026-99-99T99:99:99", null};
        String[] sorts = {
                "date,desc",
                "title,asc",
                "unknownField,asc",
                "title;drop table annonce,asc",
                "author.id,desc",
                null
        };
        String[] sizes = {"10", "0", "-1", "1000000", "abc", null};
        String[] pages = {"0", "1", "-1", "999999999", "abc", null};
        String[] qs = {"", "velo", "x".repeat(2000), null};
        String[] annonceBodies = {
                "{\"title\":\"Titre\",\"description\":\"Desc\",\"adress\":\"Adr\",\"mail\":\"ok@example.com\",\"categoryId\":%d}".formatted(category.getId()),
                "{\"title\":\"\",\"description\":\"\",\"adress\":\"\",\"mail\":\"bad\",\"categoryId\":null}",
                "{\"title\":123,\"description\":true,\"adress\":[],\"mail\":{},\"categoryId\":\"abc\"}",
                "{\"title\":\"A\"",
                "{}",
                ""
        };
        String[] patchBodies = {
                "{\"action\":\"publish\"}",
                "{\"action\":\"archive\"}",
                "{\"action\":\"invalid\"}",
                "{\"action\":\"\"}",
                "{}",
                "{\"action\"",
                ""
        };
        String[] contentTypes = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.TEXT_PLAIN_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                null
        };

        for (int i = 0; i < 70; i++) {
            MockHttpServletRequestBuilder req = get("/api/annonces");
            req = maybeParam(req, "status", statuses[random.nextInt(statuses.length)]);
            req = maybeParam(req, "categoryId", categoryIds[random.nextInt(categoryIds.length)]);
            req = maybeParam(req, "authorId", authorIds[random.nextInt(authorIds.length)]);
            req = maybeParam(req, "fromDate", dates[random.nextInt(dates.length)]);
            req = maybeParam(req, "toDate", dates[random.nextInt(dates.length)]);
            req = maybeParam(req, "sort", sorts[random.nextInt(sorts.length)]);
            req = maybeParam(req, "size", sizes[random.nextInt(sizes.length)]);
            req = maybeParam(req, "page", pages[random.nextInt(pages.length)]);
            req = maybeParam(req, "q", qs[random.nextInt(qs.length)]);
            req = withMaybeBearer(req, maybeTokens[random.nextInt(maybeTokens.length)]);
            requests.add(new RequestCase("list-fuzz-" + i, req));
        }

        for (int i = 0; i < 30; i++) {
            requests.add(new RequestCase("getById-fuzz-" + i, withMaybeBearer(
                    get("/api/annonces/" + idCandidates[random.nextInt(idCandidates.length)]),
                    maybeTokens[random.nextInt(maybeTokens.length)])));
        }

        for (int i = 0; i < 30; i++) {
            MockHttpServletRequestBuilder req = post("/api/annonces")
                    .content(annonceBodies[random.nextInt(annonceBodies.length)]);
            req = withMaybeContentType(req, contentTypes[random.nextInt(contentTypes.length)]);
            req = withMaybeBearer(req, maybeTokens[random.nextInt(maybeTokens.length)]);
            requests.add(new RequestCase("create-fuzz-" + i, req));
        }

        for (int i = 0; i < 30; i++) {
            String id = idCandidates[random.nextInt(idCandidates.length)];
            MockHttpServletRequestBuilder req = put("/api/annonces/" + id)
                    .content(annonceBodies[random.nextInt(annonceBodies.length)]);
            req = withMaybeContentType(req, contentTypes[random.nextInt(contentTypes.length)]);
            req = withMaybeBearer(req, maybeTokens[random.nextInt(maybeTokens.length)]);
            requests.add(new RequestCase("update-fuzz-" + i, req));
        }

        for (int i = 0; i < 30; i++) {
            String id = idCandidates[random.nextInt(idCandidates.length)];
            MockHttpServletRequestBuilder req = patch("/api/annonces/" + id)
                    .content(patchBodies[random.nextInt(patchBodies.length)]);
            req = withMaybeContentType(req, contentTypes[random.nextInt(contentTypes.length)]);
            req = withMaybeBearer(req, maybeTokens[random.nextInt(maybeTokens.length)]);
            requests.add(new RequestCase("patch-fuzz-" + i, req));
        }

        for (int i = 0; i < 30; i++) {
            String id = idCandidates[random.nextInt(idCandidates.length)];
            MockHttpServletRequestBuilder req = delete("/api/annonces/" + id);
            req = withMaybeBearer(req, maybeTokens[random.nextInt(maybeTokens.length)]);
            requests.add(new RequestCase("delete-fuzz-" + i, req));
        }

        assertAllNo5xx(requests);
    }

    private User buildUser(String username, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(role);
        return userRepository.save(user);
    }

    private Annonce saveAnnonce(User author, Category annonceCategory, AnnonceStatus status, String title) {
        Annonce annonce = new Annonce();
        annonce.setTitle(title);
        annonce.setDescription("Description " + title);
        annonce.setAdress("10 rue de Paris");
        annonce.setMail("contact@example.com");
        annonce.setStatus(status);
        annonce.setAuthor(author);
        annonce.setCategory(annonceCategory);
        return annonceRepository.save(annonce);
    }

    private String tokenFor(User user) {
        Set<String> roles = user.resolveRoleNames();
        Set<String> authorities = user.resolveAuthorities();
        return jwtService.generateToken(user.getId(), user.getUsername(), roles, authorities);
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

    private MockHttpServletRequestBuilder withMaybeContentType(MockHttpServletRequestBuilder request, String contentType) {
        if (contentType == null) {
            return request;
        }
        return request.contentType(contentType);
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
