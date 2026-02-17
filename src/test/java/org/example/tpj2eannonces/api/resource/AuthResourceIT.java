package org.example.tpj2eannonces.api.resource;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.service.UserService;
import org.example.tpj2eannonces.utils.JPAUtil;
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

class AuthResourceIT extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig()
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
        UserService userService = new UserService();
        cleanDatabase();
        userService.create(new User("admin", "admin@test.com", "password123"));
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

    @Test
    void login_shouldReturn200WithToken() {
        LoginDTO loginDTO = new LoginDTO("admin", "password123");

        Response response = target("/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(loginDTO));

        assertThat(response.getStatus()).isEqualTo(200);
        LoginResponseDTO body = response.readEntity(LoginResponseDTO.class);
        assertThat(body.token()).isNotNull().isNotBlank();
        assertThat(body.expiresIn()).isEqualTo(3600);
        response.close();
    }

    @Test
    void login_shouldReturn401ForInvalidCredentials() {
        LoginDTO loginDTO = new LoginDTO("admin", "wrongpassword");

        Response response = target("/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(loginDTO));

        assertThat(response.getStatus()).isEqualTo(401);
        ApiErrorDTO error = response.readEntity(ApiErrorDTO.class);
        assertThat(error.error()).isEqualTo("UNAUTHORIZED");
        response.close();
    }

    @Test
    void login_shouldReturn401ForNonexistentUser() {
        LoginDTO loginDTO = new LoginDTO("nobody", "password");

        Response response = target("/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(loginDTO));

        assertThat(response.getStatus()).isEqualTo(401);
        response.close();
    }

}
