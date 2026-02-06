package org.example.tpj2eannonces.repository;

import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class AnnonceRepository extends GenericRepository<Annonce> {

    public AnnonceRepository() {
        super(Annonce.class);
    }

    public List<Annonce> searchByKeyword(String keyword) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE " +
                    "LOWER(a.title) LIKE LOWER(:keyword) OR " +
                    "LOWER(a.description) LIKE LOWER(:keyword) " +
                    "ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> searchByKeyword(String keyword, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE " +
                    "LOWER(a.title) LIKE LOWER(:keyword) OR " +
                    "LOWER(a.description) LIKE LOWER(:keyword) " +
                    "ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("keyword", "%" + keyword + "%");
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByStatus(AnnonceStatus status) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByStatus(AnnonceStatus status, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("status", status);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByCategory(UUID categoryId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByCategory(UUID categoryId, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("categoryId", categoryId);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByCategoryAndStatus(UUID categoryId, AnnonceStatus status) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId AND a.status = :status ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("categoryId", categoryId);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByCategoryAndStatus(UUID categoryId, AnnonceStatus status, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId AND a.status = :status ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("categoryId", categoryId);
            query.setParameter("status", status);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByAuthor(UUID authorId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.author.id = :authorId ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("authorId", authorId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByAuthor(UUID authorId, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.author.id = :authorId ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setParameter("authorId", authorId);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findAllPublished() {
        return findByStatus(AnnonceStatus.PUBLISHED);
    }

    public List<Annonce> findAllPublished(int page, int size) {
        return findByStatus(AnnonceStatus.PUBLISHED, page, size);
    }

    public long countByStatus(AnnonceStatus status) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.status = :status";
            return em.createQuery(jpql, Long.class)
                    .setParameter("status", status)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public long countByCategory(UUID categoryId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :categoryId";
            return em.createQuery(jpql, Long.class)
                    .setParameter("categoryId", categoryId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Annonce> findAll() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Annonce> findAll(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category ORDER BY a.date DESC";
            TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
