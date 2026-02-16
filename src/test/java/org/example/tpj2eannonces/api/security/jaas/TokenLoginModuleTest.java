package org.example.tpj2eannonces.api.security.jaas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.UUID;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.example.tpj2eannonces.api.security.TokenStore;
import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.junit.jupiter.api.Test;

class TokenLoginModuleTest {

    @Test
    void loginAndCommit_shouldPopulateSubjectWithValidToken() throws LoginException {
        UUID userId = UUID.randomUUID();
        String token = TokenStore.getInstance().generateToken(userId, "testuser");

        Subject subject = new Subject();
        TokenLoginModule module = new TokenLoginModule();
        module.initialize(subject, new TokenCallbackHandler(token), new HashMap<>(), new HashMap<>());

        assertThat(module.login()).isTrue();
        assertThat(module.commit()).isTrue();

        assertThat(subject.getPrincipals(UserPrincipal.class)).hasSize(1);
        assertThat(subject.getPrincipals(RolePrincipal.class)).hasSize(1);

        UserPrincipal principal = subject.getPrincipals(UserPrincipal.class).iterator().next();
        assertThat(principal.getUserId()).isEqualTo(userId);
        assertThat(principal.getName()).isEqualTo("testuser");
    }

    @Test
    void login_shouldFailForInvalidToken() {
        Subject subject = new Subject();
        TokenLoginModule module = new TokenLoginModule();
        module.initialize(subject, new TokenCallbackHandler("invalid-token"), new HashMap<>(), new HashMap<>());

        assertThatThrownBy(module::login)
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Token invalide");
    }

    @Test
    void login_shouldFailForBlankToken() {
        Subject subject = new Subject();
        TokenLoginModule module = new TokenLoginModule();
        module.initialize(subject, new TokenCallbackHandler(""), new HashMap<>(), new HashMap<>());

        assertThatThrownBy(module::login)
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Token requis");
    }

    @Test
    void commit_shouldReturnFalseIfLoginNotCalled() throws LoginException {
        Subject subject = new Subject();
        TokenLoginModule module = new TokenLoginModule();
        module.initialize(subject, new TokenCallbackHandler("token"), new HashMap<>(), new HashMap<>());

        assertThat(module.commit()).isFalse();
    }

    @Test
    void logout_shouldRemovePrincipals() throws LoginException {
        UUID userId = UUID.randomUUID();
        String token = TokenStore.getInstance().generateToken(userId, "testuser");

        Subject subject = new Subject();
        TokenLoginModule module = new TokenLoginModule();
        module.initialize(subject, new TokenCallbackHandler(token), new HashMap<>(), new HashMap<>());

        module.login();
        module.commit();
        module.logout();

        assertThat(subject.getPrincipals(UserPrincipal.class)).isEmpty();
        assertThat(subject.getPrincipals(RolePrincipal.class)).isEmpty();
    }

    @Test
    void abort_shouldReturnFalseIfLoginNotAttempted() throws LoginException {
        Subject subject = new Subject();
        TokenLoginModule module = new TokenLoginModule();
        module.initialize(subject, new TokenCallbackHandler("token"), new HashMap<>(), new HashMap<>());

        assertThat(module.abort()).isFalse();
    }

    @Test
    void abort_shouldCleanupAfterLogin() throws LoginException {
        UUID userId = UUID.randomUUID();
        String token = TokenStore.getInstance().generateToken(userId, "testuser");

        Subject subject = new Subject();
        TokenLoginModule module = new TokenLoginModule();
        module.initialize(subject, new TokenCallbackHandler(token), new HashMap<>(), new HashMap<>());

        module.login();
        assertThat(module.abort()).isTrue();
    }
}
