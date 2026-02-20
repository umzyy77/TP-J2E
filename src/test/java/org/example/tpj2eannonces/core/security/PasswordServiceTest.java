package org.example.tpj2eannonces.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService(new BCryptPasswordEncoder());
    }

    @Test
    void shouldEncodeAndMatchPassword() {
        String encoded = passwordService.encode("secret");

        assertThat(encoded).isNotBlank();
        assertThat(passwordService.matches("secret", encoded)).isTrue();
    }

    @Test
    void shouldNotMatchWhenPasswordIsDifferent() {
        String encoded = passwordService.encode("secret");

        assertThat(passwordService.matches("other", encoded)).isFalse();
    }

    @Test
    void shouldReturnFalseWhenInputIsInvalid() {
        assertThat(passwordService.matches(null, "hash")).isFalse();
        assertThat(passwordService.matches("secret", null)).isFalse();
        assertThat(passwordService.matches("secret", "   ")).isFalse();
    }
}
