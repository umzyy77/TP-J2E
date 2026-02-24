package org.example.tpj2eannonces.core.web.exception;

import java.util.List;

import org.example.tpj2eannonces.core.web.dto.ApiErrorDTO;
import org.example.tpj2eannonces.features.annonce.exception.AnnonceForbiddenException;
import org.example.tpj2eannonces.features.annonce.exception.AnnonceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusinessException_shouldReturnCorrectStatus_notFound() {
        AnnonceNotFoundException ex = new AnnonceNotFoundException("not found");

        ResponseEntity<ApiErrorDTO> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assert response.getBody() != null;
        assertThat(response.getBody().error()).isEqualTo("NOT_FOUND");
        assertThat(response.getBody().messages()).containsExactly("not found");
    }

    @Test
    void handleBusinessException_shouldReturnCorrectStatus_forbidden() {
        AnnonceForbiddenException ex = new AnnonceForbiddenException("forbidden");

        ResponseEntity<ApiErrorDTO> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assert response.getBody() != null;
        assertThat(response.getBody().error()).isEqualTo("FORBIDDEN");
    }

    @Test
    void handleIllegalArgument_shouldReturn400() {
        IllegalArgumentException ex = new IllegalArgumentException("bad arg");

        ResponseEntity<ApiErrorDTO> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assert response.getBody() != null;
        assertThat(response.getBody().error()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().messages()).containsExactly("bad arg");
    }

    @Test
    void handleIllegalState_shouldReturn409() {
        IllegalStateException ex = new IllegalStateException("conflict");

        ResponseEntity<ApiErrorDTO> response = handler.handleIllegalState(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assert response.getBody() != null;
        assertThat(response.getBody().error()).isEqualTo("CONFLICT");
        assertThat(response.getBody().messages()).containsExactly("conflict");
    }

    @Test
    void handleUnexpected_shouldReturn500() {
        Exception ex = new RuntimeException("something broke");

        ResponseEntity<ApiErrorDTO> response = handler.handleUnexpected(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assert response.getBody() != null;
        assertThat(response.getBody().error()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().messages()).containsExactly("Erreur interne du serveur");
    }

    @Test
    void handleValidation_shouldReturn400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "title", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiErrorDTO> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assert response.getBody() != null;
        assertThat(response.getBody().error()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().messages()).containsExactly("title: must not be blank");
    }
}
