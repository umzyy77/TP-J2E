package org.example.tpj2eannonces.repository;

import java.util.List;
import java.util.Optional;
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
    private static final String PARAM_CATEGORY_ID = "categoryId";

    public AnnonceRepository() {
        super(Annonce.class);
    }

    public Annonce saveWithRelations(EntityManager em, Annonce annonce, UUID authorId, UUID categoryId) {
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
        return annonce;
    }

    public Annonce updateStatus(EntityManager em, UUID annonceId, AnnonceStatus targetStatus) {
        Annonce annonce = em.find(Annonce.class, annonceId);
        if (annonce == null) {
            throw new RepositoryException("Annonce non trouvée: " + annonceId);
        }
        annonce.setStatus(targetStatus);
        return annonce;
    }

    public List<Annonce> searchByKeyword(EntityManager em, String keyword, int page, int size) {
        String jpql = "SELECT a FROM Annonce a " +
                "LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category " +
                "WHERE LOWER(a.title) LIKE LOWER(:keyword) OR " +
                "LOWER(a.description) LIKE LOWER(:keyword) " +
                "ORDER BY a.date DESC";
        TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
        query.setParameter(PARAM_KEYWORD, "%" + keyword + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public List<Annonce> findByStatus(EntityManager em, AnnonceStatus status, int page, int size) {
        String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.status = :status ORDER BY a.date DESC";
        TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
        query.setParameter(PARAM_STATUS, status);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public List<Annonce> findByAuthor(EntityManager em, UUID authorId, int page, int size) {
        String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.author.id = :authorId ORDER BY a.date DESC";
        TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
        query.setParameter(PARAM_AUTHOR_ID, authorId);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long countByStatus(EntityManager em, AnnonceStatus status) {
        String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.status = :status";
        return em.createQuery(jpql, Long.class)
                .setParameter(PARAM_STATUS, status)
                .getSingleResult();
    }

    public Optional<Annonce> findByIdWithRelations(EntityManager em, UUID id) {
        String jpql = "SELECT a FROM Annonce a " +
                "LEFT JOIN FETCH a.author " +
                "LEFT JOIN FETCH a.category " +
                "WHERE a.id = :id";
        return em.createQuery(jpql, Annonce.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public List<Annonce> findByCategory(EntityManager em, UUID categoryId, int page, int size) {
        String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.category.id = :categoryId ORDER BY a.date DESC";
        TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
        query.setParameter(PARAM_CATEGORY_ID, categoryId);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long countByCategory(EntityManager em, UUID categoryId) {
        String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :categoryId";
        return em.createQuery(jpql, Long.class)
                .setParameter(PARAM_CATEGORY_ID, categoryId)
                .getSingleResult();
    }

    public long countByAuthor(EntityManager em, UUID authorId) {
        String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.author.id = :authorId";
        return em.createQuery(jpql, Long.class)
                .setParameter(PARAM_AUTHOR_ID, authorId)
                .getSingleResult();
    }

    public List<Annonce> findAllWithRelations(EntityManager em) {
        String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category ORDER BY a.date DESC";
        return em.createQuery(jpql, Annonce.class).getResultList();
    }

    public List<Annonce> findAllWithRelations(EntityManager em, int page, int size) {
        String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category ORDER BY a.date DESC";
        TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }
}
