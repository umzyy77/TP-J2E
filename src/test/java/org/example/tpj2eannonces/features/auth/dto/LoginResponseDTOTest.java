package org.example.tpj2eannonces.features.auth.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseDTOTest {

    @Test
    void twoArgConstructor_shouldSetDefaultsForRefreshFields() {
        LoginResponseDTO dto = new LoginResponseDTO("access-token", 3600L);

        assertThat(dto.token()).isEqualTo("access-token");
        assertThat(dto.expiresIn()).isEqualTo(3600L);
        assertThat(dto.refreshToken()).isNull();
        assertThat(dto.refreshExpiresIn()).isZero();
    }
}
