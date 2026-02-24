package org.example.tpj2eannonces.shared.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    static class TestEntity extends BaseEntity<Long> {
        private final Long id;

        TestEntity(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }
    }

    @Test
    void equals_shouldBeTrue_whenSameId() {
        TestEntity e1 = new TestEntity(1L);
        TestEntity e2 = new TestEntity(1L);

        assertThat(e1).isEqualTo(e2);
    }

    @Test
    void equals_shouldBeFalse_whenDifferentId() {
        TestEntity e1 = new TestEntity(1L);
        TestEntity e2 = new TestEntity(2L);

        assertThat(e1).isNotEqualTo(e2);
    }

    @Test
    void equals_shouldBeTrue_whenSameInstance() {
        TestEntity e1 = new TestEntity(1L);
        assertThat(e1).isEqualTo(e1);
    }

    @Test
    void equals_shouldBeFalse_whenNull() {
        TestEntity e1 = new TestEntity(1L);
        assertThat(e1).isNotEqualTo(null);
    }

    @Test
    void equals_shouldBeFalse_whenDifferentType() {
        TestEntity e1 = new TestEntity(1L);
        assertThat(e1).isNotEqualTo("not an entity");
    }

    @Test
    void equals_shouldBeTrue_whenBothIdsNull() {
        TestEntity e1 = new TestEntity(null);
        TestEntity e2 = new TestEntity(null);

        assertThat(e1).isEqualTo(e2);
    }

    @Test
    void hashCode_shouldBeEqual_whenSameId() {
        TestEntity e1 = new TestEntity(1L);
        TestEntity e2 = new TestEntity(1L);

        assertThat(e1).hasSameHashCodeAs(e2);
    }

    @Test
    void hashCode_shouldBeEqual_whenBothNull() {
        TestEntity e1 = new TestEntity(null);
        TestEntity e2 = new TestEntity(null);

        assertThat(e1).hasSameHashCodeAs(e2);
    }
}
