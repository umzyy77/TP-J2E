package org.example.tpj2eannonces.api.exception;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(JsonProcessingException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiErrorDTO.of("BAD_REQUEST", "JSON invalide : " + exception.getOriginalMessage()))
                .build();
    }
}
