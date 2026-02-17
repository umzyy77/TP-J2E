package org.example.tpj2eannonces.api.dto.annonce;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.example.tpj2eannonces.model.AnnonceStatus;
import org.junit.jupiter.api.Test;

class AnnonceSearchParamsTest {

    @Test
    void hasKeyword_shouldReturnTrueWhenNonBlankAndFalseWhenBlank() throws Exception {
        AnnonceSearchParams params = new AnnonceSearchParams();
        setField(params, "keyword", "voiture");
        assertThat(params.hasKeyword()).isTrue();

        setField(params, "keyword", "   ");
        assertThat(params.hasKeyword()).isFalse();
    }

    @Test
    void hasFilters_shouldReturnTrueWhenCategoryOrStatusPresent() throws Exception {
        AnnonceSearchParams params = new AnnonceSearchParams();
        assertThat(params.hasFilters()).isFalse();

        setField(params, "categoryId", 1L);
        assertThat(params.hasFilters()).isTrue();

        setField(params, "categoryId", null);
        setField(params, "status", AnnonceStatus.DRAFT);
        assertThat(params.hasFilters()).isTrue();
    }

    @Test
    void getters_shouldReturnInjectedValues() throws Exception {
        AnnonceSearchParams params = new AnnonceSearchParams();
        setField(params, "keyword", "test");
        setField(params, "categoryId", 2L);
        setField(params, "status", AnnonceStatus.PUBLISHED);
        setField(params, "page", 3);
        setField(params, "size", 15);

        assertThat(params.getKeyword()).isEqualTo("test");
        assertThat(params.getCategoryId()).isEqualTo(2L);
        assertThat(params.getStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
        assertThat(params.getPage()).isEqualTo(3);
        assertThat(params.getSize()).isEqualTo(15);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Class<?> current = target.getClass();
        Field field = null;

        while (current != null) {
            for (Field candidate : current.getDeclaredFields()) {
                if (candidate.getName().equals(fieldName)) {
                    field = candidate;
                    break;
                }
            }
            if (field != null) {
                break;
            }
            current = current.getSuperclass();
        }

        if (field == null) {
            throw new NoSuchFieldException(fieldName);
        }

        field.setAccessible(true);
        field.set(target, value);
    }
}
