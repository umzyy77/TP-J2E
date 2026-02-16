package org.example.tpj2eannonces.api.security.jaas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.service.UserService;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

class DbLoginModuleTest {

    @BeforeAll
    static void setUpClass() {
        JPAUtil.getEntityManagerFactory();
    }

    @AfterAll
    static void tearDownClass() {
        JPAUtil.close();
    }

    @BeforeEach
    void setUp() {
        UserService userService = new UserService();
        cleanDatabase();
        userService.create(new User("testuser", "test@test.com", "password123"));
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
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
    void loginAndCommit_shouldPopulateSubject() throws LoginException {
        Subject subject = new Subject();
        DbLoginModule module = new DbLoginModule();
        module.initialize(subject, new CredentialsCallbackHandler("testuser", "password123"), new HashMap<>(), new HashMap<>());

        assertThat(module.login()).isTrue();
        assertThat(module.commit()).isTrue();

        assertThat(subject.getPrincipals(UserPrincipal.class)).hasSize(1);
        assertThat(subject.getPrincipals(RolePrincipal.class)).hasSize(1);

        UserPrincipal principal = subject.getPrincipals(UserPrincipal.class).iterator().next();
        assertThat(principal.getName()).isEqualTo("testuser");

        RolePrincipal role = subject.getPrincipals(RolePrincipal.class).iterator().next();
        assertThat(role.getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void login_shouldFailForInvalidCredentials() {
        Subject subject = new Subject();
        DbLoginModule module = new DbLoginModule();
        module.initialize(subject, new CredentialsCallbackHandler("testuser", "wrongpassword"), new HashMap<>(), new HashMap<>());

        assertThatThrownBy(module::login)
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Identifiants invalides");
    }

    @Test
    void login_shouldFailForNonexistentUser() {
        Subject subject = new Subject();
        DbLoginModule module = new DbLoginModule();
        module.initialize(subject, new CredentialsCallbackHandler("nobody", "password"), new HashMap<>(), new HashMap<>());

        assertThatThrownBy(module::login)
                .isInstanceOf(LoginException.class);
    }

    @Test
    void commit_shouldReturnFalseIfLoginNotCalled() throws LoginException {
        Subject subject = new Subject();
        DbLoginModule module = new DbLoginModule();
        module.initialize(subject, new CredentialsCallbackHandler("testuser", "password123"), new HashMap<>(), new HashMap<>());

        assertThat(module.commit()).isFalse();
    }

    @Test
    void logout_shouldRemovePrincipals() throws LoginException {
        Subject subject = new Subject();
        DbLoginModule module = new DbLoginModule();
        module.initialize(subject, new CredentialsCallbackHandler("testuser", "password123"), new HashMap<>(), new HashMap<>());

        module.login();
        module.commit();
        assertThat(subject.getPrincipals()).isNotEmpty();

        module.logout();
        assertThat(subject.getPrincipals(UserPrincipal.class)).isEmpty();
        assertThat(subject.getPrincipals(RolePrincipal.class)).isEmpty();
    }

    @Test
    void abort_shouldCleanupAfterFailedCommit() throws LoginException {
        Subject subject = new Subject();
        DbLoginModule module = new DbLoginModule();
        module.initialize(subject, new CredentialsCallbackHandler("testuser", "password123"), new HashMap<>(), new HashMap<>());

        module.login();
        assertThat(module.abort()).isTrue();
    }

    @Test
    void abort_shouldReturnFalseIfLoginNotAttempted() throws LoginException {
        Subject subject = new Subject();
        DbLoginModule module = new DbLoginModule();
        module.initialize(subject, new CredentialsCallbackHandler("testuser", "password123"), new HashMap<>(), new HashMap<>());

        assertThat(module.abort()).isFalse();
    }
}
