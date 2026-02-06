package org.example.tpj2eannonces.repository;

import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class AnnonceRepository extends GenericRepository<Annonce> {

    private static final String PARAM_STATUS = "status";
    private static final String PARAM_AUTHOR_ID = "authorId";
    private static final String PARAM_KEYWORD = "keyword";

    public AnnonceRepository() {
        super(Annonce.class);
    }

    public Annonce saveWithRelations(Annonce annonce, UUID authorId, UUID categoryId) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();

            if (authorId != null) {
                User author = em.find(User.class, authorId);
                if (author == null) {
                    throw new RepositoryException("Auteur non trouvé: " + authorId);
                }
                annonce.setAuthor(author);
            }

            if (categoryId != null) {
                Category category = em.find(Category.class, categoryId);
                if (category == null) {
                    throw new RepositoryException("Catégorie non trouvée: " + categoryId);
                }
                annonce.setCategory(category);
            }

            em.persist(annonce);
            em.getTransaction().commit();
            return annonce;
        }
    }

    public Annonce updateStatus(UUID annonceId, AnnonceStatus targetStatus) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            Annonce annonce = em.find(Annonce.class, annonceId);
            if (annonce == null) {
                throw new RepositoryException("Annonce non trouvée: " + annonceId);
            }
            annonce.setStatus(targetStatus);
            em.getTransaction().commit();
            return annonce;
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

    public long countByStatus(AnnonceStatus status) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.status = :status";
            return em.createQuery(jpql, Long.class)
                    .setParameter(PARAM_STATUS, status)
                    .getSingleResult();
        }
    }

    public java.util.Optional<Annonce> findByIdWithRelations(java.util.UUID id) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a " +
                    "LEFT JOIN FETCH a.author " +
                    "LEFT JOIN FETCH a.category " +
                    "WHERE a.id = :id";
            return em.createQuery(jpql, Annonce.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst();
        }
    }

    public long countByAuthor(UUID authorId) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.author.id = :authorId";
            return em.createQuery(jpql, Long.class)
                    .setParameter(PARAM_AUTHOR_ID, authorId)
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
