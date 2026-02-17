package org.example.tpj2eannonces.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.example.tpj2eannonces.exception.user.DuplicateUserException;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

class UserServiceTest {

    private UserService userService;

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
        userService = new UserService();
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
            em.createQuery("DELETE FROM Category").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    void create_shouldPersistUser() {
        User user = userService.create(new User("john", "john@test.com", "password123"));

        assertThat(user.getId()).isNotNull();
        assertThat(user.getUsername()).isEqualTo("john");
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void create_shouldHashPassword() {
        User user = userService.create(new User("john", "john@test.com", "password123"));

        assertThat(user.getPassword()).startsWith("$2");
        assertThat(user.getPassword()).isNotEqualTo("password123");
    }

    @Test
    void create_shouldRejectDuplicateUsername() {
        userService.create(new User("john", "john@test.com", "password123"));
        User duplicate = new User("john", "other@test.com", "password123");

        assertThatThrownBy(() -> userService.create(duplicate))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining("nom d'utilisateur");
    }

    @Test
    void create_shouldRejectDuplicateEmail() {
        userService.create(new User("john", "john@test.com", "password123"));
        User duplicate = new User("jane", "john@test.com", "password123");

        assertThatThrownBy(() -> userService.create(duplicate))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining("email");
    }

    @Test
    void authenticate_shouldReturnUserForValidCredentials() {
        userService.create(new User("john", "john@test.com", "password123"));

        Optional<User> result = userService.authenticate("john", "password123");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("john");
    }

    @Test
    void authenticate_shouldReturnEmptyForWrongPassword() {
        userService.create(new User("john", "john@test.com", "password123"));

        Optional<User> result = userService.authenticate("john", "wrongpassword");

        assertThat(result).isEmpty();
    }

    @Test
    void authenticate_shouldReturnEmptyForNonexistentUser() {
        Optional<User> result = userService.authenticate("nonexistent", "password");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_shouldReturnUser() {
        User created = userService.create(new User("john", "john@test.com", "password123"));

        Optional<User> found = userService.findById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john");
    }

    @Test
    void findByUsername_shouldReturnUser() {
        userService.create(new User("john", "john@test.com", "password123"));

        Optional<User> found = userService.findByUsername("john");

        assertThat(found).isPresent();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userService.create(new User("john", "john@test.com", "password123"));
        userService.create(new User("jane", "jane@test.com", "password123"));

        List<User> all = userService.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void delete_shouldRemoveUser() {
        User user = userService.create(new User("john", "john@test.com", "password123"));

        boolean deleted = userService.delete(user.getId());

        assertThat(deleted).isTrue();
        assertThat(userService.findById(user.getId())).isEmpty();
    }

    @Test
    void update_shouldMergeUser() {
        User user = userService.create(new User("john", "john@test.com", "password123"));
        user.setUsername("john-updated");
        user.setEmail("john-updated@test.com");

        User updated = userService.update(user);

        assertThat(updated.getUsername()).isEqualTo("john-updated");
        assertThat(updated.getEmail()).isEqualTo("john-updated@test.com");
        assertThat(userService.findById(updated.getId())).isPresent();
        assertThat(userService.findById(updated.getId()).orElseThrow().getUsername()).isEqualTo("john-updated");
    }
}
