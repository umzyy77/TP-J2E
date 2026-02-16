package org.example.tpj2eannonces.api.exception;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.example.tpj2eannonces.exception.ConflictException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {

    @Override
    public Response toResponse(ConflictException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiErrorDTO.of("CONFLICT", exception.getMessage()))
                .build();
    }
}
