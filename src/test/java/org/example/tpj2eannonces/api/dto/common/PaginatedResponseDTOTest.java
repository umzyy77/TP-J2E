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

    @Test
    void of_shouldCalculateTotalPagesCorrectly() {
        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.of(List.of("a", "b"), 10, 0, 5);
        assertThat(dto.totalPages()).isEqualTo(2);
    }

    @Test
    void of_shouldRoundUpTotalPages() {
        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.of(List.of("a"), 11, 0, 5);
        assertThat(dto.totalPages()).isEqualTo(3);
    }

    @Test
    void of_shouldReturnOnePage_whenTotalCountEqualsPageSize() {
        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.of(List.of("a"), 5, 0, 5);
        assertThat(dto.totalPages()).isEqualTo(1);
    }

    @Test
    void of_shouldReturnZeroPages_whenTotalCountIsZero() {
        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.of(List.of(), 0, 0, 10);
        assertThat(dto.totalPages()).isZero();
        assertThat(dto.data()).isEmpty();
    }

    @Test
    void of_shouldPreserveAllFields() {
        List<String> data = List.of("a", "b", "c");
        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.of(data, 25, 2, 10);

        assertThat(dto.data()).isEqualTo(data);
        assertThat(dto.totalCount()).isEqualTo(25);
        assertThat(dto.page()).isEqualTo(2);
        assertThat(dto.pageSize()).isEqualTo(10);
        assertThat(dto.totalPages()).isEqualTo(3);
    }

    @Test
    void of_shouldReturnOnePageForSingleItem() {
        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.of(List.of("a"), 1, 0, 10);
        assertThat(dto.totalPages()).isEqualTo(1);
        assertThat(dto.data()).hasSize(1);
    }

    @Test
    void of_shouldHandleLargeTotalCount() {
        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.of(List.of(), 1_000_000, 0, 100);
        assertThat(dto.totalPages()).isEqualTo(10_000);
    }
}
