package org.example.tpj2eannonces.core.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordService passwordService;

    @Test
    void matches_shouldReturnTrue_whenPasswordMatches() {
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);

        assertThat(passwordService.matches("raw", "encoded")).isTrue();
    }

    @Test
    void matches_shouldReturnFalse_whenPasswordDoesNotMatch() {
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(false);

        assertThat(passwordService.matches("raw", "encoded")).isFalse();
    }

    @Test
    void matches_shouldReturnFalse_whenRawPasswordIsNull() {
        assertThat(passwordService.matches(null, "encoded")).isFalse();
    }

    @Test
    void matches_shouldReturnFalse_whenEncodedPasswordIsNull() {
        assertThat(passwordService.matches("raw", null)).isFalse();
    }

    @Test
    void matches_shouldReturnFalse_whenEncodedPasswordIsBlank() {
        assertThat(passwordService.matches("raw", "  ")).isFalse();
    }

    @Test
    void encode_shouldDelegateToEncoder() {
        when(passwordEncoder.encode("raw")).thenReturn("encoded");

        assertThat(passwordService.encode("raw")).isEqualTo("encoded");
    }
}
