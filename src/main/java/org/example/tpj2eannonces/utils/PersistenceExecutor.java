package org.example.tpj2eannonces.utils;

import java.util.function.Function;

import jakarta.persistence.EntityManager;

public interface PersistenceExecutor {

    <T> T inTransaction(Function<EntityManager, T> action);

    <T> T inReadOnly(Function<EntityManager, T> action);
}
