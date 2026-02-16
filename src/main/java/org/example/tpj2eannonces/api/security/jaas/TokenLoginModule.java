package org.example.tpj2eannonces.api.security.jaas;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.example.tpj2eannonces.api.security.TokenInfo;
import org.example.tpj2eannonces.api.security.TokenStore;
import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenLoginModule implements LoginModule {

    private static final Logger logger = LoggerFactory.getLogger(TokenLoginModule.class);

    private Subject subject;
    private CallbackHandler callbackHandler;

    private boolean loginSucceeded = false;
    private boolean commitSucceeded = false;

    private UserPrincipal userPrincipal;
    private RolePrincipal rolePrincipal;

    private final TokenStore tokenStore = TokenStore.getInstance();

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("token");

        try {
            callbackHandler.handle(new Callback[]{nameCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Erreur lors de la recuperation du token: " + e.getMessage());
        }

        String token = nameCallback.getName();
        if (token == null || token.isBlank()) {
            throw new LoginException("Token requis");
        }

        Optional<TokenInfo> tokenInfoOpt = tokenStore.validate(token);
        if (tokenInfoOpt.isEmpty()) {
            logger.debug("Token JAAS invalide ou expire");
            throw new LoginException("Token invalide ou expire");
        }

        TokenInfo tokenInfo = tokenInfoOpt.get();
        userPrincipal = new UserPrincipal(tokenInfo.userId(), tokenInfo.username());
        rolePrincipal = new RolePrincipal("ROLE_USER");
        loginSucceeded = true;

        logger.debug("Validation token JAAS reussie pour: {}", tokenInfo.username());
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }

        subject.getPrincipals().add(userPrincipal);
        subject.getPrincipals().add(rolePrincipal);
        commitSucceeded = true;

        logger.debug("Commit JAAS token: principals ajoutes au Subject pour {}", userPrincipal.getName());
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }
        if (!commitSucceeded) {
            cleanup();
        } else {
            logout();
        }
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().remove(userPrincipal);
        subject.getPrincipals().remove(rolePrincipal);
        cleanup();
        return true;
    }

    private void cleanup() {
        userPrincipal = null;
        rolePrincipal = null;
        loginSucceeded = false;
        commitSucceeded = false;
    }
}
