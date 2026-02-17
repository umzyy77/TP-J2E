package org.example.tpj2eannonces.api.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.example.tpj2eannonces.api.dto.annonce.AnnonceCreateDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceResponseDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceStatusDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceUpdateDTO;
import org.example.tpj2eannonces.api.dto.auth.LoginDTO;
import org.example.tpj2eannonces.api.dto.auth.LoginResponseDTO;
import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.example.tpj2eannonces.api.config.ObjectMapperProvider;
import org.example.tpj2eannonces.api.exception.ConflictExceptionMapper;
import org.example.tpj2eannonces.api.exception.ForbiddenExceptionMapper;
import org.example.tpj2eannonces.api.exception.GenericExceptionMapper;
import org.example.tpj2eannonces.api.exception.JsonParseExceptionMapper;
import org.example.tpj2eannonces.api.exception.NotFoundExceptionMapper;
import org.example.tpj2eannonces.api.exception.ValidationExceptionMapper;
import org.example.tpj2eannonces.api.security.SecurityFilter;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.service.CategoryService;
import org.example.tpj2eannonces.service.UserService;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

class AnnonceResourceIT extends JerseyTest {

    private User testUser;
    private Category testCategory;
    private String authToken;

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(AnnonceResource.class)
                .register(AuthResource.class)
                .register(SecurityFilter.class)
                .register(ValidationExceptionMapper.class)
                .register(NotFoundExceptionMapper.class)
                .register(ConflictExceptionMapper.class)
                .register(ForbiddenExceptionMapper.class)
                .register(GenericExceptionMapper.class)
                .register(JsonParseExceptionMapper.class)
                .register(JacksonFeature.class)
                .register(ObjectMapperProvider.class);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.connectorProvider(new HttpUrlConnectorProvider().useSetMethodWorkaround());
    }

    @BeforeAll
    static void setUpClass() {
        JPAUtil.getEntityManagerFactory();
    }

    @AfterAll
    static void tearDownClass() {
        JPAUtil.close();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        cleanDatabase();
        UserService userService = new UserService();
        CategoryService categoryService = new CategoryService();
        testUser = userService.create(new User("testuser", "test@test.com", "password123"));
        testCategory = categoryService.create(new Category("Immobilier"));
        authToken = login();
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception {
        cleanDatabase();
        super.tearDown();
    }

    private void cleanDatabase() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Annonce").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Category").executeUpdate();
            em.getTransaction().commit();
        }
    }

    private String login() {
        Response response = target("/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new LoginDTO("testuser", "password123")));
        LoginResponseDTO body = response.readEntity(LoginResponseDTO.class);
        response.close();
        return body.token();
    }

    private Long createAnnonceViaApi(String title) {
        AnnonceCreateDTO dto = new AnnonceCreateDTO(
                title, "Description", "Adresse", "mail@test.com", testCategory.getId());
        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .post(Entity.json(dto));
        AnnonceResponseDTO body = response.readEntity(AnnonceResponseDTO.class);
        response.close();
        return body.id();
    }

    // --- GET /annonces ---

    @Test
    void getList_shouldReturn200WithPaginatedResponse() {
        createAnnonceViaApi("Test1");
        createAnnonceViaApi("Test2");

        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.readEntity(Map.class);
        assertThat(body).containsKey("data")
                .containsKey("totalCount");
        response.close();
    }

    @Test
    void getList_shouldSupportPagination() {
        for (int i = 0; i < 15; i++) {
            createAnnonceViaApi("Annonce" + i);
        }

        Response response = target("/annonces")
                .queryParam("page", 0)
                .queryParam("size", 5)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        response.close();
    }

    @Test
    void getList_shouldSupportKeywordSearch() {
        createAnnonceViaApi("Voiture rouge");
        createAnnonceViaApi("Appartement centre");

        Response response = target("/annonces")
                .queryParam("q", "voiture")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.readEntity(Map.class);
        assertThat(body).containsKey("data");
        response.close();
    }

    @Test
    void getList_shouldSupportFilters() {
        createAnnonceViaApi("Draft annonce");
        Long idPublished = createAnnonceViaApi("Published annonce");
        target("/annonces/" + idPublished)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .method("PATCH", Entity.json(new AnnonceStatusDTO("publish")));

        Response response = target("/annonces")
                .queryParam("category", testCategory.getId())
                .queryParam("status", AnnonceStatus.DRAFT.name())
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.readEntity(Map.class);
        assertThat(body).containsKey("data");
        response.close();
    }

    @Test
    void getList_shouldReturn400ForInvalidPaginationParams() {
        Response response = target("/annonces")
                .queryParam("page", -1)
                .queryParam("size", 0)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(400);
        ApiErrorDTO error = response.readEntity(ApiErrorDTO.class);
        assertThat(error.error()).isEqualTo("VALIDATION_ERROR");
        response.close();
    }

    // --- GET /annonces/{id} ---

    @Test
    void getById_shouldReturn200ForExistingAnnonce() {
        Long id = createAnnonceViaApi("Test");

        Response response = target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        AnnonceResponseDTO body = response.readEntity(AnnonceResponseDTO.class);
        assertThat(body.title()).isEqualTo("Test");
        assertThat(body.status()).isEqualTo("DRAFT");
        response.close();
    }

    @Test
    void getById_shouldReturn404ForNonexistent() {
        Response response = target("/annonces/99999")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
        ApiErrorDTO error = response.readEntity(ApiErrorDTO.class);
        assertThat(error.error()).isEqualTo("NOT_FOUND");
        response.close();
    }

    // --- POST /annonces ---

    @Test
    void create_shouldReturn201WithLocationHeader() {
        AnnonceCreateDTO dto = new AnnonceCreateDTO(
                "Titre", "Desc", "Adresse", "mail@test.com", testCategory.getId());

        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .post(Entity.json(dto));

        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getLocation()).isNotNull();
        AnnonceResponseDTO body = response.readEntity(AnnonceResponseDTO.class);
        assertThat(body.title()).isEqualTo("Titre");
        assertThat(body.status()).isEqualTo("DRAFT");
        assertThat(body.author()).isNotNull();
        assertThat(body.author().id()).isEqualTo(testUser.getId());
        response.close();
    }

    @Test
    void create_shouldReturn401WithoutToken() {
        AnnonceCreateDTO dto = new AnnonceCreateDTO(
                "Titre", "Desc", "Adresse", "mail@test.com", testCategory.getId());

        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(dto));

        assertThat(response.getStatus()).isEqualTo(401);
        response.close();
    }

    @Test
    void create_shouldReturn401ForInvalidBearerFormat() {
        AnnonceCreateDTO dto = new AnnonceCreateDTO(
                "Titre", "Desc", "Adresse", "mail@test.com", testCategory.getId());

        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic xyz")
                .post(Entity.json(dto));

        assertThat(response.getStatus()).isEqualTo(401);
        ApiErrorDTO error = response.readEntity(ApiErrorDTO.class);
        assertThat(error.error()).isEqualTo("UNAUTHORIZED");
        response.close();
    }

    // --- PUT /annonces/{id} ---

    @Test
    void update_shouldReturn200ForOwner() {
        Long id = createAnnonceViaApi("Old");
        AnnonceUpdateDTO dto = new AnnonceUpdateDTO("New", "New desc", "New addr", "new@test.com", testCategory.getId());

        Response response = target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .put(Entity.json(dto));

        assertThat(response.getStatus()).isEqualTo(200);
        AnnonceResponseDTO body = response.readEntity(AnnonceResponseDTO.class);
        assertThat(body.title()).isEqualTo("New");
        response.close();
    }

    @Test
    void update_shouldReturn404ForNonexistent() {
        AnnonceUpdateDTO dto = new AnnonceUpdateDTO("New", "Desc", "Addr", "m@test.com", testCategory.getId());

        Response response = target("/annonces/99999")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .put(Entity.json(dto));

        assertThat(response.getStatus()).isEqualTo(404);
        ApiErrorDTO error = response.readEntity(ApiErrorDTO.class);
        assertThat(error.error()).isEqualTo("NOT_FOUND");
        response.close();
    }

    @Test
    void update_shouldReturn409ForPublishedAnnonce() {
        Long id = createAnnonceViaApi("Titre");
        // Publish the annonce
        target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .method("PATCH", Entity.json(new AnnonceStatusDTO("publish")));

        AnnonceUpdateDTO dto = new AnnonceUpdateDTO("New", "Desc", "Addr", "m@test.com", testCategory.getId());

        Response response = target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .put(Entity.json(dto));

        assertThat(response.getStatus()).isEqualTo(409);
        response.close();
    }

    // --- PATCH /annonces/{id} ---

    @Test
    void changeStatus_shouldReturn200ForPublish() {
        Long id = createAnnonceViaApi("Titre");

        Response response = target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .method("PATCH", Entity.json(new AnnonceStatusDTO("publish")));

        assertThat(response.getStatus()).isEqualTo(200);
        AnnonceResponseDTO body = response.readEntity(AnnonceResponseDTO.class);
        assertThat(body.status()).isEqualTo("PUBLISHED");
        response.close();
    }

    @Test
    void changeStatus_shouldReturn200ForArchive() {
        Long id = createAnnonceViaApi("Titre");
        // Publish first
        target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .method("PATCH", Entity.json(new AnnonceStatusDTO("publish")));

        Response response = target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .method("PATCH", Entity.json(new AnnonceStatusDTO("archive")));

        assertThat(response.getStatus()).isEqualTo(200);
        AnnonceResponseDTO body = response.readEntity(AnnonceResponseDTO.class);
        assertThat(body.status()).isEqualTo("ARCHIVED");
        response.close();
    }

    @Test
    void changeStatus_shouldReturn409ForInvalidTransition() {
        Long id = createAnnonceViaApi("Titre");

        Response response = target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .method("PATCH", Entity.json(new AnnonceStatusDTO("archive")));

        assertThat(response.getStatus()).isEqualTo(409);
        response.close();
    }

    @Test
    void changeStatus_shouldReturn404ForNonexistent() {
        Response response = target("/annonces/99999")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .method("PATCH", Entity.json(new AnnonceStatusDTO("publish")));

        assertThat(response.getStatus()).isEqualTo(404);
        ApiErrorDTO error = response.readEntity(ApiErrorDTO.class);
        assertThat(error.error()).isEqualTo("NOT_FOUND");
        response.close();
    }

    // --- DELETE /annonces/{id} ---

    @Test
    void delete_shouldReturn204ForArchivedAnnonce() {
        Long id = createAnnonceViaApi("Titre");
        // Publish then archive
        target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .method("PATCH", Entity.json(new AnnonceStatusDTO("publish")));
        target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .method("PATCH", Entity.json(new AnnonceStatusDTO("archive")));

        Response response = target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .delete();

        assertThat(response.getStatus()).isEqualTo(204);
        response.close();
    }

    @Test
    void delete_shouldReturn409ForNonArchivedAnnonce() {
        Long id = createAnnonceViaApi("Titre");

        Response response = target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .delete();

        assertThat(response.getStatus()).isEqualTo(409);
        response.close();
    }

    @Test
    void delete_shouldReturn404ForNonexistent() {
        Response response = target("/annonces/99999")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .delete();

        assertThat(response.getStatus()).isEqualTo(404);
        ApiErrorDTO error = response.readEntity(ApiErrorDTO.class);
        assertThat(error.error()).isEqualTo("NOT_FOUND");
        response.close();
    }

    @Test
    void delete_shouldReturn401WithoutToken() {
        Long id = createAnnonceViaApi("Titre");

        Response response = target("/annonces/" + id)
                .request(MediaType.APPLICATION_JSON)
                .delete();

        assertThat(response.getStatus()).isEqualTo(401);
        response.close();
    }
}
