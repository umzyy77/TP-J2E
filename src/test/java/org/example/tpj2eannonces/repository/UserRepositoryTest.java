package org.example.tpj2eannonces.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

class UserRepositoryTest {

    private UserRepository repository;

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
        repository = new UserRepository();
        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Annonce").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    void save_shouldPersistUser() {
        User user = new User("testuser", "test@example.com", "password123");

        User saved = repository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findByUsername_shouldReturnUser() {
        repository.save(new User("testuser", "test@example.com", "password123"));

        Optional<User> found = repository.findByUsername("testuser");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByEmail_shouldReturnUser() {
        repository.save(new User("testuser", "test@example.com", "password123"));

        Optional<User> found = repository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void existsByUsername_shouldReturnTrue() {
        repository.save(new User("testuser", "test@example.com", "password123"));

        boolean exists = repository.existsByUsername("testuser");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse() {
        boolean exists = repository.existsByUsername("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void findByUsernameAndPassword_shouldAuthenticateUser() {
        repository.save(new User("testuser", "test@example.com", "password123"));

        Optional<User> found = repository.findByUsernameAndPassword("testuser", "password123");
        Optional<User> notFound = repository.findByUsernameAndPassword("testuser", "wrongpassword");

        assertThat(found).isPresent();
        assertThat(notFound).isEmpty();
    }
}
