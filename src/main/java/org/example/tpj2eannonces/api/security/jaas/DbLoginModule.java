package org.example.tpj2eannonces.api.security.jaas;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.repository.UserRepository;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.example.tpj2eannonces.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbLoginModule implements LoginModule {

    private static final Logger logger = LoggerFactory.getLogger(DbLoginModule.class);

    private Subject subject;
    private CallbackHandler callbackHandler;

    private boolean loginSucceeded = false;
    private boolean commitSucceeded = false;

    private UserPrincipal userPrincipal;
    private RolePrincipal rolePrincipal;

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("username");
        PasswordCallback passwordCallback = new PasswordCallback("password", false);

        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Erreur lors de la recuperation des credentials: " + e.getMessage());
        }

        String username = nameCallback.getName();
        char[] passwordChars = passwordCallback.getPassword();
        String password = passwordChars != null ? new String(passwordChars) : null;

        if (username == null || password == null) {
            throw new LoginException("Username et password requis");
        }

        Optional<User> userOpt = JPAUtil.inReadOnly(em -> userRepository.findByUsername(em, username));

        if (userOpt.isEmpty() || !PasswordUtils.verify(password, userOpt.get().getPassword())) {
            logger.debug("Echec authentification JAAS pour l'utilisateur: {}", username);
            throw new LoginException("Identifiants invalides");
        }

        User user = userOpt.get();
        userPrincipal = new UserPrincipal(user.getId(), user.getUsername());
        rolePrincipal = new RolePrincipal("ROLE_USER");
        loginSucceeded = true;

        logger.debug("Authentification JAAS reussie pour: {}", username);
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

        logger.debug("Commit JAAS: principals ajoutes au Subject pour {}", userPrincipal.getName());
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
