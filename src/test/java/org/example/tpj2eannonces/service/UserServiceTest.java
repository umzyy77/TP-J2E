package org.example.tpj2eannonces.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.example.tpj2eannonces.exception.user.DuplicateUserException;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.repository.UserRepository;
import org.example.tpj2eannonces.utils.PasswordUtils;
import org.example.tpj2eannonces.utils.PersistenceExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PersistenceExecutor persistenceExecutor;

    @Mock
    private EntityManager entityManager;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(repository, persistenceExecutor);
        stubPersistenceExecution();
    }

    @Test
    void constructor_withRepositoryOnly_shouldCreateService() {
        UserService service = new UserService(repository);

        assertThat(service).isNotNull();
    }

    @Test
    void create_shouldHashPasswordAndPersistUser() {
        User user = new User("john", "john@test.com", "password123");
        when(repository.existsByUsername(entityManager, "john")).thenReturn(false);
        when(repository.existsByEmail(entityManager, "john@test.com")).thenReturn(false);
        when(repository.save(eq(entityManager), any(User.class))).thenAnswer(invocation -> invocation.getArgument(1));

        User created = userService.create(user);

        assertThat(created.getPassword()).startsWith("$2");
        assertThat(created.getPassword()).isNotEqualTo("password123");
        verify(repository).save(eq(entityManager), any(User.class));
    }

    @Test
    void create_shouldRejectDuplicateUsername() {
        User duplicate = new User("john", "john@test.com", "password123");
        when(repository.existsByUsername(entityManager, "john")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(duplicate))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining("nom d'utilisateur");

        verify(repository, never()).existsByEmail(any(), any());
        verify(repository, never()).save(any(), any());
    }

    @Test
    void authenticate_shouldReturnUserForValidCredentials() {
        User user = new User("john", "john@test.com", PasswordUtils.hash("password123"));
        when(repository.findByUsername(entityManager, "john")).thenReturn(Optional.of(user));

        Optional<User> result = userService.authenticate("john", "password123");

        assertThat(result).contains(user);
    }

    @Test
    void authenticate_shouldReturnEmptyForWrongPassword() {
        User user = new User("john", "john@test.com", PasswordUtils.hash("password123"));
        when(repository.findByUsername(entityManager, "john")).thenReturn(Optional.of(user));

        Optional<User> result = userService.authenticate("john", "wrong");

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldDelegateToRepository() {
        List<User> users = List.of(new User("john", "john@test.com", "x"));
        when(repository.findAllOrderByCreatedAt(entityManager)).thenReturn(users);

        List<User> result = userService.findAll();

        assertThat(result).containsExactlyElementsOf(users);
        verify(repository).findAllOrderByCreatedAt(entityManager);
    }

    @Test
    void delete_shouldDelegateToRepository() {
        UUID userId = UUID.randomUUID();
        when(repository.deleteById(entityManager, userId)).thenReturn(true);

        boolean deleted = userService.delete(userId);

        assertThat(deleted).isTrue();
        verify(repository).deleteById(entityManager, userId);
    }

    private void stubPersistenceExecution() {
        lenient().when(persistenceExecutor.inTransaction(any())).thenAnswer(invocation -> {
            Function<EntityManager, Object> action = invocation.getArgument(0);
            return action.apply(entityManager);
        });
        lenient().when(persistenceExecutor.inReadOnly(any())).thenAnswer(invocation -> {
            Function<EntityManager, Object> action = invocation.getArgument(0);
            return action.apply(entityManager);
        });
    }
}
