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

class UserRepositoryIT {

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
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Annonce").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
        }
    }

    private User saveUser() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            User user = new User("testuser", "test@example.com", "password123");
            repository.save(em, user);
            em.getTransaction().commit();
            return user;
        }
    }

    @Test
    void save_shouldPersistUser() {
        User saved = saveUser();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findByUsername_shouldReturnUser() {
        saveUser();

        try (EntityManager em = JPAUtil.getEntityManager()) {
            Optional<User> found = repository.findByUsername(em, "testuser");
            assertThat(found).isPresent();
            assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        }
    }

    @Test
    void findByEmail_shouldReturnUser() {
        saveUser();

        try (EntityManager em = JPAUtil.getEntityManager()) {
            Optional<User> found = repository.findByEmail(em, "test@example.com");
            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("testuser");
        }
    }

    @Test
    void existsByUsername_shouldReturnTrue() {
        saveUser();

        try (EntityManager em = JPAUtil.getEntityManager()) {
            boolean exists = repository.existsByUsername(em, "testuser");
            assertThat(exists).isTrue();
        }
    }

    @Test
    void existsByUsername_shouldReturnFalse() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            boolean exists = repository.existsByUsername(em, "nonexistent");
            assertThat(exists).isFalse();
        }
    }
}

