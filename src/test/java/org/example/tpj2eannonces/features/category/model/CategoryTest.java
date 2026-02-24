package org.example.tpj2eannonces.features.category.model;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    void defaultConstructor_shouldWork() {
        Category cat = new Category();
        assertThat(cat.getLabel()).isNull();
        assertThat(cat.getAnnonces()).isEmpty();
    }

    @Test
    void parameterizedConstructor_shouldSetLabel() {
        Category cat = new Category("Immobilier");
        assertThat(cat.getLabel()).isEqualTo("Immobilier");
    }

    @Test
    void gettersAndSetters_shouldWork() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setLabel("Auto");
        cat.setAnnonces(new ArrayList<>());

        assertThat(cat.getId()).isEqualTo(1L);
        assertThat(cat.getLabel()).isEqualTo("Auto");
        assertThat(cat.getAnnonces()).isEmpty();
    }

    @Test
    void equals_shouldBeTrue_whenSameId() {
        Category c1 = new Category();
        c1.setId(1L);
        Category c2 = new Category();
        c2.setId(1L);

        assertThat(c1).isEqualTo(c2);
    }

    @Test
    void equals_shouldBeFalse_whenDifferentId() {
        Category c1 = new Category();
        c1.setId(1L);
        Category c2 = new Category();
        c2.setId(2L);

        assertThat(c1).isNotEqualTo(c2);
    }

    @Test
    void equals_shouldBeFalse_whenNotCategory() {
        Category c1 = new Category();
        c1.setId(1L);
        assertThat(c1).isNotEqualTo("not a category");
    }

    @Test
    void hashCode_shouldBeConsistent() {
        Category c1 = new Category();
        c1.setId(1L);
        Category c2 = new Category();
        c2.setId(1L);

        assertThat(c1).hasSameHashCodeAs(c2);
    }

    @Test
    void toString_shouldContainFields() {
        Category cat = new Category("Immobilier");
        cat.setId(1L);

        String result = cat.toString();
        assertThat(result).contains("id=1", "Immobilier");
    }
}
