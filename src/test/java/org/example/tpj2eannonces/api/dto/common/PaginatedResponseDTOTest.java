package org.example.tpj2eannonces.api.dto.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class PaginatedResponseDTOTest {

    @Test
    void of_shouldReturnZeroTotalPagesWhenPageSizeIsZero() {
        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.of(List.of("a"), 10, 0, 0);
        assertThat(dto.totalPages()).isZero();
    }
}
