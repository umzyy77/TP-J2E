package org.example.tpj2eannonces.api.security.jaas;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.example.tpj2eannonces.api.security.UserPrincipal;

public abstract class AbstractLoginModule implements LoginModule {

    protected Subject subject;
    protected CallbackHandler callbackHandler;

    private boolean loginSucceeded = false;
    private boolean commitSucceeded = false;

    private UserPrincipal userPrincipal;
    private RolePrincipal rolePrincipal;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    protected abstract LoginResult authenticate() throws LoginException;

    @Override
    public boolean login() throws LoginException {
        LoginResult result = authenticate();
        userPrincipal = result.userPrincipal();
        rolePrincipal = result.rolePrincipal();
        loginSucceeded = true;
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

    protected record LoginResult(UserPrincipal userPrincipal, RolePrincipal rolePrincipal) {
    }
}
