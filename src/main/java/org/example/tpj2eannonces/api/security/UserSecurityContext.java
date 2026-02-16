package org.example.tpj2eannonces.api.security;

import java.security.Principal;

import jakarta.ws.rs.core.SecurityContext;

/**
 * SecurityContext JAX-RS custom qui propage l'identite de l'utilisateur
 * authentifie via token Bearer a travers toute la chaine de traitement.
 *
 * C'est le mecanisme standard JAX-RS pour acceder a l'utilisateur
 * courant dans les resources via {@code @Context SecurityContext}.
 */
public class UserSecurityContext implements SecurityContext {

    private final UserPrincipal principal;
    private final boolean secure;

    public UserSecurityContext(TokenInfo tokenInfo, boolean secure) {
        this.principal = new UserPrincipal(tokenInfo.userId(), tokenInfo.username());
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}
