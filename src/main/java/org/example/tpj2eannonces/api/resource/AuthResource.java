package org.example.tpj2eannonces.api.resource;

import org.example.tpj2eannonces.api.dto.auth.LoginDTO;
import org.example.tpj2eannonces.api.dto.auth.LoginResponseDTO;
import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.example.tpj2eannonces.api.security.TokenStore;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.service.UserService;

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

    private final UserService userService = new UserService();
    private final TokenStore tokenStore = TokenStore.getInstance();

    @POST
    @Path("/login")
    public Response login(@Valid LoginDTO dto) {
        return userService.authenticate(dto.username(), dto.password())
                .map(this::buildLoginResponse)
                .orElseGet(() -> Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiErrorDTO.of("UNAUTHORIZED", "Identifiants invalides"))
                        .build());
    }

    private Response buildLoginResponse(User user) {
        String token = tokenStore.generateToken(user.getId(), user.getUsername());
        long expiresIn = tokenStore.getTokenTtlSeconds();
        return Response.ok(new LoginResponseDTO(token, expiresIn)).build();
    }
}
