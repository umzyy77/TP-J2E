package org.example.tpj2eannonces.api.security;

import java.security.Principal;

import javax.security.auth.Subject;

import org.example.tpj2eannonces.api.security.jaas.RolePrincipal;

import jakarta.ws.rs.core.SecurityContext;

/**
 * SecurityContext JAX-RS custom qui propage l'identite de l'utilisateur
 * authentifie via JAAS a travers toute la chaine de traitement.
 *
 * Construit a partir du Subject JAAS contenant UserPrincipal et RolePrincipal.
 */
public class UserSecurityContext implements SecurityContext {

    private final Subject subject;
    private final UserPrincipal principal;
    private final boolean secure;

    public UserSecurityContext(Subject subject, boolean secure) {
        this.subject = subject;
        this.secure = secure;
        this.principal = subject.getPrincipals(UserPrincipal.class).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("UserPrincipal absent du Subject JAAS"));
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return subject.getPrincipals(RolePrincipal.class).stream()
                .anyMatch(p -> p.getName().equals(role));
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }

    public Subject getSubject() {
        return subject;
    }
}
