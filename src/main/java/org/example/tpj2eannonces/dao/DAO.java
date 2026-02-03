package org.example.tpj2eannonces.dao;

import java.util.List;

public abstract class DAO<T> {
    public abstract T create(T obj);

    public abstract T update(T obj);

    public abstract boolean delete(java.util.UUID id);

    public abstract T find(java.util.UUID id);

    public abstract List<T> list();
}
