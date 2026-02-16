package org.example.tpj2eannonces.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordUtilsTest {

    @Test
    void hash_shouldReturnBcryptHash() {
        String hash = PasswordUtils.hash("password123");

        assertThat(hash).isNotNull()
                .startsWith("$2");
    }

    @Test
    void verify_shouldReturnTrueForCorrectPassword() {
        String hash = PasswordUtils.hash("myPassword");

        assertThat(PasswordUtils.verify("myPassword", hash)).isTrue();
    }

    @Test
    void verify_shouldReturnFalseForWrongPassword() {
        String hash = PasswordUtils.hash("myPassword");

        assertThat(PasswordUtils.verify("wrongPassword", hash)).isFalse();
    }

    @Test
    void verify_shouldReturnFalseForNullPassword() {
        assertThat(PasswordUtils.verify(null, "$2a$12$hash")).isFalse();
    }

    @Test
    void verify_shouldReturnFalseForNullHash() {
        assertThat(PasswordUtils.verify("password", null)).isFalse();
    }

    @Test
    void verify_shouldHandlePlaintextFallback() {
        assertThat(PasswordUtils.verify("plaintext", "plaintext")).isTrue();
        assertThat(PasswordUtils.verify("plaintext", "wrong")).isFalse();
    }

    @Test
    void verify_shouldHandleInvalidBcryptHash() {
        assertThat(PasswordUtils.verify("password", "$2invalid")).isFalse();
    }
}
