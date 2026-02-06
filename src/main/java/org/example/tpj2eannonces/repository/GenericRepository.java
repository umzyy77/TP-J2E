package org.example.tpj2eannonces.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.utils.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public abstract class GenericRepository<T> {

    private final Class<T> entityClass;

    protected GenericRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    public T save(T entity) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        }
    }

    public T update(T entity) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            T merged = em.merge(entity);
            em.getTransaction().commit();
            return merged;
        }
    }

    public boolean deleteById(UUID id) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        }
    }

    public Optional<T> findById(UUID id) {
        try (EntityManager em = getEntityManager()) {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    public List<T> findAll() {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        }
    }

    public long count() {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, Long.class).getSingleResult();
        }
    }

    public List<T> findAll(int page, int size) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }
}
