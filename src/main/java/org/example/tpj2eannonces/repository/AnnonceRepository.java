package org.example.tpj2eannonces.repository;

import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class AnnonceRepository extends GenericRepository<Annonce> {

    private static final String PARAM_STATUS = "status";
    private static final String PARAM_CATEGORY_ID = "categoryId";
    private static final String PARAM_AUTHOR_ID = "authorId";
    private static final String PARAM_KEYWORD = "keyword";

    public AnnonceRepository() {
        super(Annonce.class);
    }

    public List<Annonce> searchByKeyword(String keyword) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE " +
                    "LOWER(a.title) LIKE LOWER(:keyword) OR " +
                    "LOWER(a.description) LIKE LOWER(:keyword) " +
                    "ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_KEYWORD, "%" + keyword + "%");
            return query.getResultList();
        }
    }

    public List<Annonce> searchByKeyword(String keyword, int page, int size) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE " +
                    "LOWER(a.title) LIKE LOWER(:keyword) OR " +
                    "LOWER(a.description) LIKE LOWER(:keyword) " +
                    "ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_KEYWORD, "%" + keyword + "%");
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }

    public List<Annonce> findByStatus(AnnonceStatus status) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_STATUS, status);
            return query.getResultList();
        }
    }

    public List<Annonce> findByStatus(AnnonceStatus status, int page, int size) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_STATUS, status);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }

    public List<Annonce> findByCategory(UUID categoryId) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_CATEGORY_ID, categoryId);
            return query.getResultList();
        }
    }

    public List<Annonce> findByCategory(UUID categoryId, int page, int size) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_CATEGORY_ID, categoryId);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }

    public List<Annonce> findByCategoryAndStatus(UUID categoryId, AnnonceStatus status) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId AND a.status = :status ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_CATEGORY_ID, categoryId);
            query.setParameter(PARAM_STATUS, status);
            return query.getResultList();
        }
    }

    public List<Annonce> findByCategoryAndStatus(UUID categoryId, AnnonceStatus status, int page, int size) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId AND a.status = :status ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_CATEGORY_ID, categoryId);
            query.setParameter(PARAM_STATUS, status);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }

    public List<Annonce> findByAuthor(UUID authorId) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.author.id = :authorId ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_AUTHOR_ID, authorId);
            return query.getResultList();
        }
    }

    public List<Annonce> findByAuthor(UUID authorId, int page, int size) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.author.id = :authorId ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter(PARAM_AUTHOR_ID, authorId);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }

    public List<Annonce> findAllPublished() {
        return findByStatus(AnnonceStatus.PUBLISHED);
    }

    public List<Annonce> findAllPublished(int page, int size) {
        return findByStatus(AnnonceStatus.PUBLISHED, page, size);
    }

    public long countByStatus(AnnonceStatus status) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.status = :status";
            return em.createQuery(jpql, Long.class)
                    .setParameter(PARAM_STATUS, status)
                    .getSingleResult();
        }
    }

    public long countByCategory(UUID categoryId) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :categoryId";
            return em.createQuery(jpql, Long.class)
                    .setParameter(PARAM_CATEGORY_ID, categoryId)
                    .getSingleResult();
        }
    }

    @Override
    public List<Annonce> findAll() {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class).getResultList();
        }
    }

    @Override
    public List<Annonce> findAll(int page, int size) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }
}
