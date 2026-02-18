package org.example.tpj2eannonces.api.security.jaas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;

import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.repository.UserRepository;
import org.example.tpj2eannonces.utils.PasswordUtils;
import org.example.tpj2eannonces.utils.PersistenceExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class DbLoginModuleTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PersistenceExecutor persistenceExecutor;

    @Mock
    private EntityManager entityManager;

    private DbLoginModule module;

    @BeforeEach
    void setUp() {
        module = new DbLoginModule(userRepository, persistenceExecutor);
        lenient().when(persistenceExecutor.inReadOnly(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Function<EntityManager, Object> action = invocation.getArgument(0);
            return action.apply(entityManager);
        });
    }

    @Test
    void loginAndCommit_shouldPopulateSubjectForValidCredentials() throws LoginException {
        User user = new User("testuser", "test@test.com", PasswordUtils.hash("password123"));
        user.setId(UUID.randomUUID());
        when(userRepository.findByUsername(entityManager, "testuser")).thenReturn(Optional.of(user));

        Subject subject = new Subject();
        module.initialize(subject, new CredentialsCallbackHandler("testuser", "password123"), new HashMap<>(), new HashMap<>());

        assertThat(module.login()).isTrue();
        assertThat(module.commit()).isTrue();

        assertThat(subject.getPrincipals(UserPrincipal.class)).hasSize(1);
        assertThat(subject.getPrincipals(RolePrincipal.class)).hasSize(1);
    }

    @Test
    void login_shouldFailForInvalidCredentials() {
        User user = new User("testuser", "test@test.com", PasswordUtils.hash("password123"));
        when(userRepository.findByUsername(entityManager, "testuser")).thenReturn(Optional.of(user));

        Subject subject = new Subject();
        module.initialize(subject, new CredentialsCallbackHandler("testuser", "wrong"), new HashMap<>(), new HashMap<>());

        assertThatThrownBy(module::login)
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Identifiants invalides");
    }

    @Test
    void login_shouldFailForUnknownUser() {
        when(userRepository.findByUsername(entityManager, "unknown")).thenReturn(Optional.empty());

        Subject subject = new Subject();
        module.initialize(subject, new CredentialsCallbackHandler("unknown", "password123"), new HashMap<>(), new HashMap<>());

        assertThatThrownBy(module::login)
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Identifiants invalides");
    }

    @ParameterizedTest
    @MethodSource("nullCredentialCases")
    void login_shouldFailWhenCredentialsAreMissing(String username, String password) {
        Subject subject = new Subject();
        module.initialize(subject, credentialsCallbackHandler(username, password), new HashMap<>(), new HashMap<>());

        assertThatThrownBy(module::login)
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Username et password requis");
    }

    @Test
    void login_shouldFailForCallbackError() {
        CallbackHandler brokenHandler = _ -> {
            throw new IOException("boom");
        };

        Subject subject = new Subject();
        module.initialize(subject, brokenHandler, new HashMap<>(), new HashMap<>());

        assertThatThrownBy(module::login)
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Erreur lors de la recuperation des credentials");
    }

    private CallbackHandler credentialsCallbackHandler(String username, String password) {
        return callbacks -> {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback nameCallback) {
                    nameCallback.setName(username);
                } else if (callback instanceof PasswordCallback passwordCallback) {
                    passwordCallback.setPassword(password != null ? password.toCharArray() : null);
                }
            }
        };
    }

    private static Stream<Arguments> nullCredentialCases() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, "password123"),
                Arguments.of("testuser", null));
    }
}
