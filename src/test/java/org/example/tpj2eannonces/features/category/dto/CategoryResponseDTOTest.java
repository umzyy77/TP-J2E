package org.example.tpj2eannonces.features.category.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryResponseDTOTest {

    @Test
    void record_shouldStoreAndReturnFields() {
        CategoryResponseDTO dto = new CategoryResponseDTO(1L, "Immobilier");

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.label()).isEqualTo("Immobilier");
    }
}
