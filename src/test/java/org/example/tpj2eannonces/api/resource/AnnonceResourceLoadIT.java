package org.example.tpj2eannonces.api.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.example.tpj2eannonces.api.config.ObjectMapperProvider;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceCreateDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceResponseDTO;
import org.example.tpj2eannonces.api.dto.auth.LoginDTO;
import org.example.tpj2eannonces.api.dto.auth.LoginResponseDTO;
import org.example.tpj2eannonces.api.exception.ConflictExceptionMapper;
import org.example.tpj2eannonces.api.exception.ForbiddenExceptionMapper;
import org.example.tpj2eannonces.api.exception.GenericExceptionMapper;
import org.example.tpj2eannonces.api.exception.JsonParseExceptionMapper;
import org.example.tpj2eannonces.api.exception.NotFoundExceptionMapper;
import org.example.tpj2eannonces.api.exception.OptimisticLockExceptionMapper;
import org.example.tpj2eannonces.api.exception.RollbackExceptionMapper;
import org.example.tpj2eannonces.api.exception.ValidationExceptionMapper;
import org.example.tpj2eannonces.api.security.SecurityFilter;
import org.example.tpj2eannonces.model.Category;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Tests de charge simples pour l'API REST Annonces.
 * Verifie que l'API reste performante et stable sous charge concurrente.
 */
class AnnonceResourceLoadIT extends JerseyTest {

    private static final Logger LOG = LoggerFactory.getLogger(AnnonceResourceLoadIT.class);

    private static final int CONCURRENT_USERS = 10;
    private static final int REQUESTS_PER_USER = 5;
    private static final long MAX_AVG_RESPONSE_TIME_MS = 2000;

    private String authToken;
    private Category testCategory;

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
                .register(OptimisticLockExceptionMapper.class)
                .register(RollbackExceptionMapper.class)
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
        userService.create(new User("loaduser", "load@test.com", "password123"));
        testCategory = categoryService.create(new Category("LoadTest"));
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
                .post(Entity.json(new LoginDTO("loaduser", "password123")));
        LoginResponseDTO body = response.readEntity(LoginResponseDTO.class);
        response.close();
        return body.token();
    }

    private Long createAnnonce(String title) {
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

    // --- Test de charge : lectures concurrentes GET /annonces ---

    @Test
    void loadTest_concurrentGetList_shouldAllReturn200() throws Exception {
        // Preparer des donnees
        for (int i = 0; i < 20; i++) {
            createAnnonce("LoadAnnonce" + i);
        }

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Callable<LoadResult>> tasks = new ArrayList<>();

        for (int i = 0; i < CONCURRENT_USERS * REQUESTS_PER_USER; i++) {
            tasks.add(() -> {
                long start = System.currentTimeMillis();
                Response response = target("/annonces")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .request(MediaType.APPLICATION_JSON)
                        .get();
                long elapsed = System.currentTimeMillis() - start;
                int status = response.getStatus();
                response.close();
                return new LoadResult(status, elapsed);
            });
        }

        List<Future<LoadResult>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        List<LoadResult> results = new ArrayList<>();
        for (Future<LoadResult> f : futures) {
            results.add(f.get());
        }

        long successCount = results.stream().filter(r -> r.status == 200).count();
        double avgTime = results.stream().mapToLong(r -> r.durationMs).average().orElse(0);
        long maxTime = results.stream().mapToLong(r -> r.durationMs).max().orElse(0);

        LOG.info("GET /annonces - Requetes: {}, Succes: {}, Temps moyen: {}ms, Temps max: {}ms",
                results.size(), successCount, String.format("%.1f", avgTime), maxTime);

        assertThat(successCount).isEqualTo(results.size());
        assertThat(avgTime).isLessThan(MAX_AVG_RESPONSE_TIME_MS);
    }

    // --- Test de charge : creations concurrentes POST /annonces ---

    @Test
    void loadTest_concurrentCreate_shouldAllReturn201() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Callable<LoadResult>> tasks = new ArrayList<>();

        for (int i = 0; i < CONCURRENT_USERS * REQUESTS_PER_USER; i++) {
            final int idx = i;
            tasks.add(() -> {
                AnnonceCreateDTO dto = new AnnonceCreateDTO(
                        "Concurrent" + idx, "Desc", "Addr", "m@t.com", testCategory.getId());
                long start = System.currentTimeMillis();
                Response response = target("/annonces")
                        .request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .post(Entity.json(dto));
                long elapsed = System.currentTimeMillis() - start;
                int status = response.getStatus();
                response.close();
                return new LoadResult(status, elapsed);
            });
        }

        List<Future<LoadResult>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        List<LoadResult> results = new ArrayList<>();
        for (Future<LoadResult> f : futures) {
            results.add(f.get());
        }

        long successCount = results.stream().filter(r -> r.status == 201).count();
        double avgTime = results.stream().mapToLong(r -> r.durationMs).average().orElse(0);
        long maxTime = results.stream().mapToLong(r -> r.durationMs).max().orElse(0);

        LOG.info("POST /annonces - Requetes: {}, Succes: {}, Temps moyen: {}ms, Temps max: {}ms",
                results.size(), successCount, String.format("%.1f", avgTime), maxTime);

        assertThat(successCount).isEqualTo(results.size());
        assertThat(avgTime).isLessThan(MAX_AVG_RESPONSE_TIME_MS);
    }

    // --- Test de charge : lectures concurrentes GET /annonces/{id} ---

    @Test
    void loadTest_concurrentGetById_shouldAllReturn200() throws Exception {
        Long annonceId = createAnnonce("TargetAnnonce");

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Callable<LoadResult>> tasks = new ArrayList<>();

        for (int i = 0; i < CONCURRENT_USERS * REQUESTS_PER_USER; i++) {
            tasks.add(() -> {
                long start = System.currentTimeMillis();
                Response response = target("/annonces/" + annonceId)
                        .request(MediaType.APPLICATION_JSON)
                        .get();
                long elapsed = System.currentTimeMillis() - start;
                int status = response.getStatus();
                response.close();
                return new LoadResult(status, elapsed);
            });
        }

        List<Future<LoadResult>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        List<LoadResult> results = new ArrayList<>();
        for (Future<LoadResult> f : futures) {
            results.add(f.get());
        }

        long successCount = results.stream().filter(r -> r.status == 200).count();
        double avgTime = results.stream().mapToLong(r -> r.durationMs).average().orElse(0);
        long maxTime = results.stream().mapToLong(r -> r.durationMs).max().orElse(0);

        LOG.info("GET /annonces/{} - Requetes: {}, Succes: {}, Temps moyen: {}ms, Temps max: {}ms",
                annonceId, results.size(), successCount, String.format("%.1f", avgTime), maxTime);

        assertThat(successCount).isEqualTo(results.size());
        assertThat(avgTime).isLessThan(MAX_AVG_RESPONSE_TIME_MS);
    }

    // --- Test de charge mixte : lectures + ecritures concurrentes ---

    @Test
    void loadTest_mixedReadWrite_shouldRemainStable() throws Exception {
        for (int i = 0; i < 10; i++) {
            createAnnonce("Existing" + i);
        }

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Callable<LoadResult>> tasks = new ArrayList<>();

        // Moitie lectures, moitie ecritures
        for (int i = 0; i < CONCURRENT_USERS * REQUESTS_PER_USER; i++) {
            final int idx = i;
            if (idx % 2 == 0) {
                // Lecture
                tasks.add(() -> {
                    long start = System.currentTimeMillis();
                    Response response = target("/annonces")
                            .queryParam("page", 0)
                            .queryParam("size", 10)
                            .request(MediaType.APPLICATION_JSON)
                            .get();
                    long elapsed = System.currentTimeMillis() - start;
                    int status = response.getStatus();
                    response.close();
                    return new LoadResult(status, elapsed);
                });
            } else {
                // Ecriture
                tasks.add(() -> {
                    AnnonceCreateDTO dto = new AnnonceCreateDTO(
                            "Mixed" + idx, "Desc", "Addr", "m@t.com", testCategory.getId());
                    long start = System.currentTimeMillis();
                    Response response = target("/annonces")
                            .request(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken)
                            .post(Entity.json(dto));
                    long elapsed = System.currentTimeMillis() - start;
                    int status = response.getStatus();
                    response.close();
                    return new LoadResult(status, elapsed);
                });
            }
        }

        List<Future<LoadResult>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        List<LoadResult> results = new ArrayList<>();
        for (Future<LoadResult> f : futures) {
            results.add(f.get());
        }

        long successCount = results.stream().filter(r -> r.status == 200 || r.status == 201).count();
        double avgTime = results.stream().mapToLong(r -> r.durationMs).average().orElse(0);
        long maxTime = results.stream().mapToLong(r -> r.durationMs).max().orElse(0);
        long failCount = results.size() - successCount;

        LOG.info("Mixed R/W - Requetes: {}, Succes: {}, Echecs: {}, Temps moyen: {}ms, Temps max: {}ms",
                results.size(), successCount, failCount, String.format("%.1f", avgTime), maxTime);

        assertThat(successCount).isEqualTo(results.size());
        assertThat(avgTime).isLessThan(MAX_AVG_RESPONSE_TIME_MS);
    }

    private record LoadResult(int status, long durationMs) {
    }
}
