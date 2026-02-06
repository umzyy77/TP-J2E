package org.example.tpj2eannonces.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public List<Category> findAllOrderByLabel() {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT c FROM Category c ORDER BY c.label";
            return em.createQuery(jpql, Category.class).getResultList();
        }
    }

    public long countAnnoncesByCategory(UUID categoryId) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :id";
            return em.createQuery(jpql, Long.class)
                    .setParameter("id", categoryId)
                    .getSingleResult();
        }
    }
}
