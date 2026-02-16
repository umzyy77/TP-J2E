package org.example.tpj2eannonces.api.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.example.tpj2eannonces.exception.ConflictException;
import org.example.tpj2eannonces.exception.ForbiddenException;
import org.example.tpj2eannonces.exception.NotFoundException;
import org.example.tpj2eannonces.exception.annonce.AnnonceImmutableException;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

class ExceptionMappersTest {

    @Test
    void notFoundExceptionMapper_shouldReturn404() {
        NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();

        try (Response response = mapper.toResponse(new NotFoundException("Annonce non trouvee: 42"))) {
            assertThat(response.getStatus()).isEqualTo(404);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("NOT_FOUND");
            assertThat(error.messages()).contains("Annonce non trouvee: 42");
        }
    }

    @Test
    void conflictExceptionMapper_shouldReturn409() {
        ConflictExceptionMapper mapper = new ConflictExceptionMapper();

        try (Response response = mapper.toResponse(new ConflictException("Conflit") {})) {
            assertThat(response.getStatus()).isEqualTo(409);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("CONFLICT");
        }
    }

    @Test
    void conflictExceptionMapper_shouldHandleSubclasses() {
        ConflictExceptionMapper mapper = new ConflictExceptionMapper();

        try (Response response = mapper.toResponse(new AnnonceImmutableException())) {
            assertThat(response.getStatus()).isEqualTo(409);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("CONFLICT");
        }
    }

    @Test
    void forbiddenExceptionMapper_shouldReturn403() {
        ForbiddenExceptionMapper mapper = new ForbiddenExceptionMapper();

        try (Response response = mapper.toResponse(new ForbiddenException("Non autorise"))) {
            assertThat(response.getStatus()).isEqualTo(403);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("FORBIDDEN");
            assertThat(error.messages()).contains("Non autorise");
        }
    }

    @Test
    void genericExceptionMapper_shouldReturn500() {
        GenericExceptionMapper mapper = new GenericExceptionMapper();

        try (Response response = mapper.toResponse(new RuntimeException("Unexpected"))) {
            assertThat(response.getStatus()).isEqualTo(500);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("INTERNAL_ERROR");
        }
    }

    @Test
    void jsonParseExceptionMapper_shouldReturn400() {
        JsonParseExceptionMapper mapper = new JsonParseExceptionMapper();

        try (Response response = mapper.toResponse(new JsonParseException(null, "bad json"))) {
            assertThat(response.getStatus()).isEqualTo(400);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("BAD_REQUEST");
        }
    }

    @Test
    void validationExceptionMapper_shouldReturn400() {
        ValidationExceptionMapper mapper = new ValidationExceptionMapper();
        Set<ConstraintViolation<?>> violations = Set.of();
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", violations);

        try (Response response = mapper.toResponse(ex)) {
            assertThat(response.getStatus()).isEqualTo(400);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("VALIDATION_ERROR");
        }
    }
}
