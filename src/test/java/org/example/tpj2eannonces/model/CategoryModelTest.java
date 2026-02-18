package org.example.tpj2eannonces.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class CategoryModelTest {

    @Test
    void equals_shouldReturnFalseWhenOtherIsNotCategory() {
        Category category = new Category("Immobilier");
        assertThat(category.equals("not-category")).isFalse();
    }

    @Test
    void toString_shouldContainLabel() {
        Category category = new Category("Services");
        String text = category.toString();
        assertThat(text).contains("Services");
    }

    @Test
    void setAnnoncesAndGetAnnonces_shouldWork() {
        Category category = new Category("Auto");
        List<Annonce> annonces = new ArrayList<>();
        annonces.add(new Annonce("T1", "D1", "A1", "m1@test.com"));

        category.setAnnonces(annonces);

        assertThat(category.getAnnonces()).hasSize(1);
    }

    @Test
    void hashCode_shouldBeEqualForCategoriesWithSameId() {
        Category first = new Category("Auto");
        Category second = new Category("Services");
        first.setId(10L);
        second.setId(10L);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }
}
