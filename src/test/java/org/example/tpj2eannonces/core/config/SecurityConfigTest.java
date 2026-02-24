package org.example.tpj2eannonces.core.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityConfigTest {

    @Test
    void passwordEncoder_shouldThrow_whenBcryptStrengthTooLow() {
        SecurityConfig config = new SecurityConfig(null, null);

        assertThatThrownBy(() -> config.passwordEncoder(3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("doit etre entre 4 et 31");
    }

    @Test
    void passwordEncoder_shouldThrow_whenBcryptStrengthTooHigh() {
        SecurityConfig config = new SecurityConfig(null, null);

        assertThatThrownBy(() -> config.passwordEncoder(32))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("doit etre entre 4 et 31");
    }

    @Test
    void passwordEncoder_shouldReturnEncoder_whenStrengthIsValid() {
        SecurityConfig config = new SecurityConfig(null, null);

        assertThat(config.passwordEncoder(12)).isNotNull();
    }
}
