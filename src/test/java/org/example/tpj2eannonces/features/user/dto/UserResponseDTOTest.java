package org.example.tpj2eannonces.features.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseDTOTest {

    @Test
    void record_shouldStoreAndReturnFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0);

        UserResponseDTO dto = new UserResponseDTO(id, "alice", "alice@test.com", "ROLE_USER", createdAt);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.username()).isEqualTo("alice");
        assertThat(dto.email()).isEqualTo("alice@test.com");
        assertThat(dto.role()).isEqualTo("ROLE_USER");
        assertThat(dto.createdAt()).isEqualTo(createdAt);
    }
}
