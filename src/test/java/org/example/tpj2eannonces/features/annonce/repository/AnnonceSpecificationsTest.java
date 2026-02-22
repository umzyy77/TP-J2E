package org.example.tpj2eannonces.features.annonce.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;

class AnnonceSpecificationsTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"  ", "test"})
    void hasKeyword_shouldReturnSpec(String keyword) {
        Specification<Annonce> spec = AnnonceSpecifications.hasKeyword(keyword);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasStatus_null_shouldReturnUnrestricted() {
        Specification<Annonce> spec = AnnonceSpecifications.hasStatus(null);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasStatus_withValue_shouldReturnSpec() {
        Specification<Annonce> spec = AnnonceSpecifications.hasStatus(AnnonceStatus.DRAFT);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasCategoryId_null_shouldReturnUnrestricted() {
        Specification<Annonce> spec = AnnonceSpecifications.hasCategoryId(null);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasCategoryId_withValue_shouldReturnSpec() {
        Specification<Annonce> spec = AnnonceSpecifications.hasCategoryId(1L);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasAuthorId_null_shouldReturnUnrestricted() {
        Specification<Annonce> spec = AnnonceSpecifications.hasAuthorId(null);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasAuthorId_withValue_shouldReturnSpec() {
        Specification<Annonce> spec = AnnonceSpecifications.hasAuthorId(UUID.randomUUID());
        assertThat(spec).isNotNull();
    }

    @Test
    void createdAfter_null_shouldReturnUnrestricted() {
        Specification<Annonce> spec = AnnonceSpecifications.createdAfter(null);
        assertThat(spec).isNotNull();
    }

    @Test
    void createdAfter_withValue_shouldReturnSpec() {
        Specification<Annonce> spec = AnnonceSpecifications.createdAfter(LocalDateTime.now());
        assertThat(spec).isNotNull();
    }

    @Test
    void createdBefore_null_shouldReturnUnrestricted() {
        Specification<Annonce> spec = AnnonceSpecifications.createdBefore(null);
        assertThat(spec).isNotNull();
    }

    @Test
    void createdBefore_withValue_shouldReturnSpec() {
        Specification<Annonce> spec = AnnonceSpecifications.createdBefore(LocalDateTime.now());
        assertThat(spec).isNotNull();
    }
}
