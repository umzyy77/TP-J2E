package org.example.tpj2eannonces.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public abstract class GenericRepository<T, I> {

    private final Class<T> entityClass;

    protected GenericRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(EntityManager em, T entity) {
        em.persist(entity);
        return entity;
    }

    public T update(EntityManager em, T entity) {
        return em.merge(entity);
    }

    public boolean deleteById(EntityManager em, I id) {
        T entity = em.find(entityClass, id);
        if (entity != null) {
            em.remove(entity);
            return true;
        }
        return false;
    }

    public Optional<T> findById(EntityManager em, I id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    public List<T> findAll(EntityManager em) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(jpql, entityClass).getResultList();
    }

    public long count(EntityManager em) {
        String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    protected <Q> void applyPagination(TypedQuery<Q> query, int page, int size) {
        query.setFirstResult(page * size);
        query.setMaxResults(size);
    }
}
