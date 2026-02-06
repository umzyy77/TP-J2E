package org.example.tpj2eannonces.repository;

import java.util.Optional;

import org.example.tpj2eannonces.model.Category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class CategoryRepository extends GenericRepository<Category> {

    public CategoryRepository() {
        super(Category.class);
    }

    public Optional<Category> findByLabel(String label) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT c FROM Category c WHERE c.label = :label";
            TypedQuery<Category> query = em.createQuery(jpql, Category.class);
            query.setParameter("label", label);
            return query.getResultStream().findFirst();
        }
    }

    public boolean existsByLabel(String label) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT COUNT(c) FROM Category c WHERE c.label = :label";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("label", label)
                    .getSingleResult();
            return count > 0;
        }
    }
}
