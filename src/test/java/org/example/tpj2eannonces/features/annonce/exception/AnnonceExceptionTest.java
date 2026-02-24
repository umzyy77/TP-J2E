package org.example.tpj2eannonces.features.annonce.exception;

import org.example.tpj2eannonces.features.auth.exception.AuthUnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class AnnonceExceptionTest {

    @Test
    void annonceNotFoundException_shouldHave404() {
        AnnonceNotFoundException ex = new AnnonceNotFoundException("not found");

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(ex.getMessage()).isEqualTo("not found");
    }

    @Test
    void annonceForbiddenException_shouldHave403() {
        AnnonceForbiddenException ex = new AnnonceForbiddenException("forbidden");

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(ex.getErrorCode()).isEqualTo("FORBIDDEN");
        assertThat(ex.getMessage()).isEqualTo("forbidden");
    }

    @Test
    void authUnauthorizedException_shouldHave401() {
        AuthUnauthorizedException ex = new AuthUnauthorizedException("unauthorized");

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ex.getErrorCode()).isEqualTo("UNAUTHORIZED");
        assertThat(ex.getMessage()).isEqualTo("unauthorized");
    }
}
