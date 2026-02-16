package org.example.tpj2eannonces.api.resource;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.example.tpj2eannonces.api.dto.auth.LoginDTO;
import org.example.tpj2eannonces.api.dto.auth.LoginResponseDTO;
import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.example.tpj2eannonces.api.security.TokenStore;
import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.example.tpj2eannonces.api.security.jaas.CredentialsCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class AuthResource {

    private static final Logger logger = LoggerFactory.getLogger(AuthResource.class);

    private final TokenStore tokenStore = TokenStore.getInstance();

    @POST
    @Path("/login")
    public Response login(@Valid LoginDTO dto) {
        try {
            LoginContext lc = new LoginContext(
                    "MasterAnnonceLogin",
                    new CredentialsCallbackHandler(dto.username(), dto.password()));
            lc.login();

            Subject subject = lc.getSubject();
            UserPrincipal userPrincipal = extractUserPrincipal(subject);

            String token = tokenStore.generateToken(userPrincipal.getUserId(), userPrincipal.getName());
            long expiresIn = tokenStore.getTokenTtlSeconds();

            logger.debug("Login JAAS reussi pour: {}", userPrincipal.getName());
            return Response.ok(new LoginResponseDTO(token, expiresIn)).build();

        } catch (LoginException e) {
            logger.debug("Echec login JAAS: {}", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiErrorDTO.of("UNAUTHORIZED", "Identifiants invalides"))
                    .build();
        }
    }

    private UserPrincipal extractUserPrincipal(Subject subject) {
        return subject.getPrincipals(UserPrincipal.class).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("UserPrincipal absent du Subject JAAS"));
    }
}
