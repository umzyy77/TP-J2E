package org.example.tpj2eannonces.api.security;

import java.io.IOException;
import java.lang.reflect.Method;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;

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

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Context
    private ResourceInfo resourceInfo;

    private final TokenStore tokenStore = TokenStore.getInstance();

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

        tokenStore.validate(token).ifPresentOrElse(
                info -> requestContext.setSecurityContext(new UserSecurityContext(info, isSecure)),
                () -> abort(requestContext, "Token invalide ou expiré")
        );
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
