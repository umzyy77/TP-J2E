package org.example.tpj2eannonces.features.annonce.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnonceSearchDTOTest {

    @Test
    void hasFilters_shouldReturnFalse_whenAllNull() {
        AnnonceSearchDTO dto = new AnnonceSearchDTO(null, null, null, null, null, null);
        assertThat(dto.hasFilters()).isFalse();
    }

    @Test
    void hasFilters_shouldReturnTrue_whenQIsSet() {
        AnnonceSearchDTO dto = new AnnonceSearchDTO("keyword", null, null, null, null, null);
        assertThat(dto.hasFilters()).isTrue();
    }

    @Test
    void hasFilters_shouldReturnTrue_whenStatusIsSet() {
        AnnonceSearchDTO dto = new AnnonceSearchDTO(null, AnnonceStatus.DRAFT, null, null, null, null);
        assertThat(dto.hasFilters()).isTrue();
    }

    @Test
    void hasFilters_shouldReturnTrue_whenCategoryIdIsSet() {
        AnnonceSearchDTO dto = new AnnonceSearchDTO(null, null, 1L, null, null, null);
        assertThat(dto.hasFilters()).isTrue();
    }

    @Test
    void hasFilters_shouldReturnTrue_whenAuthorIdIsSet() {
        AnnonceSearchDTO dto = new AnnonceSearchDTO(null, null, null, UUID.randomUUID(), null, null);
        assertThat(dto.hasFilters()).isTrue();
    }

    @Test
    void hasFilters_shouldReturnTrue_whenFromDateIsSet() {
        AnnonceSearchDTO dto = new AnnonceSearchDTO(null, null, null, null, LocalDateTime.now(), null);
        assertThat(dto.hasFilters()).isTrue();
    }

    @Test
    void hasFilters_shouldReturnTrue_whenToDateIsSet() {
        AnnonceSearchDTO dto = new AnnonceSearchDTO(null, null, null, null, null, LocalDateTime.now());
        assertThat(dto.hasFilters()).isTrue();
    }
}
