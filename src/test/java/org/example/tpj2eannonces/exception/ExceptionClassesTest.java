package org.example.tpj2eannonces.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.tpj2eannonces.exception.annonce.InvalidTransitionException;
import org.junit.jupiter.api.Test;

class ExceptionClassesTest {

    @Test
    void conflictException_twoArgsConstructor_shouldKeepMessageAndCause() {
        RuntimeException cause = new RuntimeException("cause");
        ConflictException exception = new ConflictException("conflict", cause);

        assertThat(exception).hasMessage("conflict");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void notFoundException_twoArgsConstructor_shouldKeepMessageAndCause() {
        RuntimeException cause = new RuntimeException("cause");
        NotFoundException exception = new NotFoundException("notfound", cause);

        assertThat(exception).hasMessage("notfound");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void invalidTransitionException_oneArgConstructor_shouldSetExpectedMessage() {
        InvalidTransitionException exception = new InvalidTransitionException("foobar");
        assertThat(exception)
                .hasMessageContaining("Action inconnue")
                .hasMessageContaining("foobar");
    }
}
