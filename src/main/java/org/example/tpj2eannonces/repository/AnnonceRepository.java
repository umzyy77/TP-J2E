package org.example.tpj2eannonces.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class AnnonceRepository extends GenericRepository<Annonce, Long> {

    private static final String PARAM_AUTHOR_ID = "authorId";

    public AnnonceRepository() {
        super(Annonce.class);
    }

    public Annonce saveWithRelations(EntityManager em, Annonce annonce, UUID authorId, Long categoryId) {
        if (authorId == null) {
            throw new RepositoryException("Auteur obligatoire");
        }
        if (categoryId == null) {
            throw new RepositoryException("Categorie obligatoire");
        }

        User author = em.find(User.class, authorId);
        if (author == null) {
            throw new RepositoryException("Auteur non trouve: " + authorId);
        }
        Category category = em.find(Category.class, categoryId);
        if (category == null) {
            throw new RepositoryException("Categorie non trouvee: " + categoryId);
        }

        annonce.setAuthor(author);
        annonce.setCategory(category);
        em.persist(annonce);
        return annonce;
    }

    public Annonce updateStatus(EntityManager em, Long annonceId, AnnonceStatus targetStatus) {
        Annonce annonce = em.find(Annonce.class, annonceId);
        if (annonce == null) {
            throw new RepositoryException("Annonce non trouvee: " + annonceId);
        }
        annonce.setStatus(targetStatus);
        return annonce;
    }

    public List<Annonce> searchByKeyword(EntityManager em, String keyword, int page, int size) {
        return findByFilters(em, keyword, null, null, page, size);
    }

    public long countByKeyword(EntityManager em, String keyword) {
        return countByFilters(em, keyword, null, null);
    }

    public List<Annonce> findByAuthor(EntityManager em, UUID authorId, int page, int size) {
        String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.author.id = :authorId ORDER BY a.date DESC";
        TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
        query.setParameter(PARAM_AUTHOR_ID, authorId);
        applyPagination(query, page, size);
        return query.getResultList();
    }

    public Optional<Annonce> findByIdWithRelations(EntityManager em, Long id) {
        String jpql = "SELECT a FROM Annonce a " +
                "LEFT JOIN FETCH a.author " +
                "LEFT JOIN FETCH a.category " +
                "WHERE a.id = :id";
        return em.createQuery(jpql, Annonce.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public List<Annonce> findByFilters(EntityManager em, Long categoryId, AnnonceStatus status, int page, int size) {
        return findByFilters(em, null, categoryId, status, page, size);
    }

    public List<Annonce> findByFilters(
            EntityManager em, String keyword, Long categoryId, AnnonceStatus status, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Annonce> cq = cb.createQuery(Annonce.class);
        Root<Annonce> root = cq.from(Annonce.class);
        root.fetch("author");
        root.fetch("category");

        List<Predicate> predicates = new ArrayList<>(2);
        addOptionalFilters(cb, root, predicates, keyword, categoryId, status);

        cq.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(root.get("date")));

        TypedQuery<Annonce> query = em.createQuery(cq);
        applyPagination(query, page, size);
        return query.getResultList();
    }

    public long countByFilters(EntityManager em, Long categoryId, AnnonceStatus status) {
        return countByFilters(em, null, categoryId, status);
    }

    public long countByFilters(EntityManager em, String keyword, Long categoryId, AnnonceStatus status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Annonce> root = cq.from(Annonce.class);

        List<Predicate> predicates = new ArrayList<>(2);
        addOptionalFilters(cb, root, predicates, keyword, categoryId, status);

        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getSingleResult();
    }

    private void addOptionalFilters(
            CriteriaBuilder cb, Root<Annonce> root, List<Predicate> predicates,
            String keyword, Long categoryId, AnnonceStatus status) {
        if (keyword != null && !keyword.isBlank()) {
            String pattern = "%" + keyword.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)));
        }
        if (categoryId != null) {
            predicates.add(cb.equal(root.get("category").get("id"), categoryId));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
    }

    public long countByAuthor(EntityManager em, UUID authorId) {
        String jpql = "SELECT COUNT(a) FROM Annonce a WHERE a.author.id = :authorId";
        return em.createQuery(jpql, Long.class)
                .setParameter(PARAM_AUTHOR_ID, authorId)
                .getSingleResult();
    }
}
