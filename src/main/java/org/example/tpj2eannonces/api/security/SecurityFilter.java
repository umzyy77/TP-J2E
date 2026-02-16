package org.example.tpj2eannonces.api.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.example.tpj2eannonces.api.security.jaas.TokenCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Priority;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        Class<?> resourceClass = resourceInfo.getResourceClass();

        if (method.isAnnotationPresent(PermitAll.class)
                || resourceClass.isAnnotationPresent(PermitAll.class)) {
            return;
        }

        String authHeader = requestContext.getHeaderString(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abort(requestContext, "Token d'authentification requis");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        boolean isSecure = "https".equalsIgnoreCase(requestContext.getUriInfo().getRequestUri().getScheme());

        try {
            LoginContext lc = new LoginContext("MasterAnnonceToken", new TokenCallbackHandler(token));
            lc.login();

            Subject subject = lc.getSubject();
            requestContext.setSecurityContext(new UserSecurityContext(subject, isSecure));

            logger.debug("Authentification JAAS token reussie pour: {}",
                    extractUserPrincipal(subject).getName());

        } catch (LoginException e) {
            logger.debug("Echec validation token JAAS: {}", e.getMessage());
            abort(requestContext, "Token invalide ou expiré");
        }
    }

    private UserPrincipal extractUserPrincipal(Subject subject) {
        Set<Principal> principals = subject.getPrincipals();
        return principals.stream()
                .filter(p -> p instanceof UserPrincipal)
                .map(p -> (UserPrincipal) p)
                .findFirst()
                .orElseThrow();
    }

    private void abort(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(ApiErrorDTO.of("UNAUTHORIZED", message))
                        .build()
        );
    }
}
