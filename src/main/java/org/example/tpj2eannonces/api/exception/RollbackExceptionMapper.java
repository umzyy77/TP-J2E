package org.example.tpj2eannonces.api.exception;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;

import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RollbackExceptionMapper implements ExceptionMapper<RollbackException> {

    private static final String CONFLICT_MESSAGE =
            "Conflit de concurrence: la ressource a ete modifiee par un autre utilisateur";

    @Override
    public Response toResponse(RollbackException exception) {
        if (containsOptimisticConflict(exception)) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ApiErrorDTO.of("CONFLICT", CONFLICT_MESSAGE))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiErrorDTO.of("INTERNAL_ERROR", "Erreur interne du serveur"))
                .build();
    }

    private boolean containsOptimisticConflict(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof OptimisticLockException) {
                return true;
            }

            String className = current.getClass().getName();
            if ("org.hibernate.StaleObjectStateException".equals(className)
                    || "org.hibernate.StaleStateException".equals(className)) {
                return true;
            }

            current = current.getCause();
        }
        return false;
    }
}
