package org.example.tpj2eannonces.api.exception;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;

import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class OptimisticLockExceptionMapper implements ExceptionMapper<OptimisticLockException> {

    private static final String CONFLICT_MESSAGE =
            "Conflit de concurrence: la ressource a ete modifiee par un autre utilisateur";

    @Override
    public Response toResponse(OptimisticLockException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiErrorDTO.of("CONFLICT", CONFLICT_MESSAGE))
                .build();
    }
}
