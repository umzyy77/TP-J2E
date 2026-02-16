package org.example.tpj2eannonces.api.security.jaas;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class CredentialsCallbackHandler implements CallbackHandler {

    private final String username;
    private final char[] password;

    public CredentialsCallbackHandler(String username, String password) {
        this.username = username;
        this.password = password != null ? password.toCharArray() : new char[0];
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            switch (callback) {
                case NameCallback nameCallback -> nameCallback.setName(username);
                case PasswordCallback passwordCallback -> passwordCallback.setPassword(password);
                default -> throw new UnsupportedCallbackException(callback,
                        "Callback non supporte: " + callback.getClass().getName());
            }
        }
    }
}
