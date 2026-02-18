package org.example.tpj2eannonces.api.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.example.tpj2eannonces.exception.ConflictException;
import org.example.tpj2eannonces.exception.ForbiddenException;
import org.example.tpj2eannonces.exception.NotFoundException;
import org.example.tpj2eannonces.exception.annonce.AnnonceImmutableException;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;

import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
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

    @Test
    void validationExceptionMapper_shouldReturnViolationMessages() {
        ValidationExceptionMapper mapper = new ValidationExceptionMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ValidatedPayload payload = new ValidatedPayload("");
        Set<ConstraintViolation<ValidatedPayload>> violations = validator.validate(payload);
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", Set.copyOf(violations));

        try (Response response = mapper.toResponse(ex)) {
            assertThat(response.getStatus()).isEqualTo(400);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("VALIDATION_ERROR");
            assertThat(error.messages()).contains("name: name required");
        }
    }

    @Test
    void optimisticLockExceptionMapper_shouldReturn409() {
        OptimisticLockExceptionMapper mapper = new OptimisticLockExceptionMapper();

        try (Response response = mapper.toResponse(new OptimisticLockException("Version stale"))) {
            assertThat(response.getStatus()).isEqualTo(409);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("CONFLICT");
        }
    }

    @Test
    void rollbackExceptionMapper_shouldReturn409WhenOptimisticConflict() {
        RollbackExceptionMapper mapper = new RollbackExceptionMapper();
        RollbackException rollback = new RollbackException(
                "Transaction rollback", new OptimisticLockException("Version stale"));

        try (Response response = mapper.toResponse(rollback)) {
            assertThat(response.getStatus()).isEqualTo(409);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("CONFLICT");
        }
    }

    @Test
    void rollbackExceptionMapper_shouldReturn500WhenNotOptimisticConflict() {
        RollbackExceptionMapper mapper = new RollbackExceptionMapper();
        RollbackException rollback = new RollbackException(
                "Transaction rollback", new RuntimeException("Other cause"));

        try (Response response = mapper.toResponse(rollback)) {
            assertThat(response.getStatus()).isEqualTo(500);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("INTERNAL_ERROR");
        }
    }

    @Test
    void rollbackExceptionMapper_shouldReturn409WhenHibernateStaleStateException() {
        RollbackExceptionMapper mapper = new RollbackExceptionMapper();
        RollbackException rollback = new RollbackException(
                "Transaction rollback", new StaleStateException("stale"));

        try (Response response = mapper.toResponse(rollback)) {
            assertThat(response.getStatus()).isEqualTo(409);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("CONFLICT");
        }
    }

    private static class ValidatedPayload {
        @NotBlank(message = "name required")
        private final String name;

        private ValidatedPayload(String name) {
            this.name = name;
        }
    }
}
