package org.example.tpj2eannonces.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BaseEntityTest {

    @Test
    void equals_shouldBeReflexive() {
        DummyEntity entity = new DummyEntity(1L);
        assertThat(entity).isEqualTo(entity);
    }

    @Test
    void equals_shouldReturnFalseForNullOrOtherType() {
        DummyEntity entity = new DummyEntity(1L);
        assertThat(entity)
                .isNotEqualTo(null)
                .isNotEqualTo("not-entity");
    }

    @Test
    void equals_shouldReturnTrueForSameIdAndFalseForDifferentId() {
        DummyEntity e1 = new DummyEntity(1L);
        DummyEntity e2 = new DummyEntity(1L);
        DummyEntity e3 = new DummyEntity(2L);

        assertThat(e1)
                .isEqualTo(e2)
                .isNotEqualTo(e3);
    }

    @Test
    void hashCode_shouldHandleNullId() {
        DummyEntity entity = new DummyEntity(null);
        assertThat(entity).hasSameHashCodeAs(new DummyEntity(null));
    }

    @Test
    void hashCode_shouldHandleNonNullId() {
        DummyEntity e1 = new DummyEntity(42L);
        DummyEntity e2 = new DummyEntity(42L);
        assertThat(e1).hasSameHashCodeAs(e2);
    }

    private static final class DummyEntity extends BaseEntity<Long> {
        private final Long id;

        private DummyEntity(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }
    }
}
