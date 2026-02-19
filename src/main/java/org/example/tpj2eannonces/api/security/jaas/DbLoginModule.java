package org.example.tpj2eannonces.api.security.jaas;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.repository.UserRepository;
import org.example.tpj2eannonces.utils.JpaPersistenceExecutor;
import org.example.tpj2eannonces.utils.PasswordUtils;
import org.example.tpj2eannonces.utils.PersistenceExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbLoginModule extends AbstractLoginModule {

    private static final Logger logger = LoggerFactory.getLogger(DbLoginModule.class);

    private final UserRepository userRepository;
    private final PersistenceExecutor persistenceExecutor;

    public DbLoginModule() {
        this(new UserRepository(), new JpaPersistenceExecutor());
    }

    DbLoginModule(UserRepository userRepository, PersistenceExecutor persistenceExecutor) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.persistenceExecutor = Objects.requireNonNull(persistenceExecutor);
    }

    @Override
    protected LoginResult authenticate() throws LoginException {
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

        Optional<User> userOpt = persistenceExecutor.inReadOnly(em -> userRepository.findByUsername(em, username));

        if (userOpt.isEmpty() || !PasswordUtils.verify(password, userOpt.get().getPassword())) {
            logger.debug("Echec authentification JAAS pour l'utilisateur: {}", username);
            throw new LoginException("Identifiants invalides");
        }

        User user = userOpt.get();
        logger.debug("Authentification JAAS reussie pour: {}", username);
        return new LoginResult(
                new UserPrincipal(user.getId(), user.getUsername()),
                new RolePrincipal("ROLE_USER"));
    }
}
