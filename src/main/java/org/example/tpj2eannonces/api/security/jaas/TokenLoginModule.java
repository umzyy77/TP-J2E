package org.example.tpj2eannonces.api.security.jaas;

import java.io.IOException;
import java.util.Optional;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.example.tpj2eannonces.api.security.TokenInfo;
import org.example.tpj2eannonces.api.security.TokenStore;
import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenLoginModule extends AbstractLoginModule {

    private static final Logger logger = LoggerFactory.getLogger(TokenLoginModule.class);
    private static final TokenStore TOKEN_STORE = new TokenStore();

    @Override
    protected LoginResult authenticate() throws LoginException {
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

        Optional<TokenInfo> tokenInfoOpt = TOKEN_STORE.validate(token);
        if (tokenInfoOpt.isEmpty()) {
            logger.debug("Token JAAS invalide ou expire");
            throw new LoginException("Token invalide ou expire");
        }

        TokenInfo tokenInfo = tokenInfoOpt.get();
        logger.debug("Validation token JAAS reussie pour: {}", tokenInfo.username());
        return new LoginResult(
                new UserPrincipal(tokenInfo.userId(), tokenInfo.username()),
                new RolePrincipal("ROLE_USER"));
    }
}
