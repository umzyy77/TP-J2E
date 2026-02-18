package org.example.tpj2eannonces.utils;

import java.util.function.Function;

import jakarta.persistence.EntityManager;

public final class JpaPersistenceExecutor implements PersistenceExecutor {

    @Override
    public <T> T inTransaction(Function<EntityManager, T> action) {
        return JPAUtil.inTransaction(action);
    }

    @Override
    public <T> T inReadOnly(Function<EntityManager, T> action) {
        return JPAUtil.inReadOnly(action);
    }
}
