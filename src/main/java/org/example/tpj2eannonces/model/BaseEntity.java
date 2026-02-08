package org.example.tpj2eannonces.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public abstract class BaseEntity<I> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public abstract I getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
