package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.utils.JPAUtil;

import jakarta.persistence.EntityManager;

public class AnnonceService {

    private static final String PARAM_STATUS = "status";

    public Annonce create(Annonce annonce) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(annonce);
            em.getTransaction().commit();
            return annonce;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la création de l'annonce", e);
        }
    }

    public Annonce create(Annonce annonce, UUID authorId, UUID categoryId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();

            if (authorId != null) {
                User author = em.find(User.class, authorId);
                if (author == null) {
                    throw new ServiceException("Auteur non trouvé: " + authorId);
                }
                annonce.setAuthor(author);
            }

            if (categoryId != null) {
                Category category = em.find(Category.class, categoryId);
                if (category == null) {
                    throw new ServiceException("Catégorie non trouvée: " + categoryId);
                }
                annonce.setCategory(category);
            }

            em.persist(annonce);
            em.getTransaction().commit();
            return annonce;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la création de l'annonce", e);
        }
    }

    public Annonce update(Annonce annonce) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            Annonce merged = em.merge(annonce);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la mise à jour de l'annonce", e);
        }
    }

    public Annonce changeStatus(UUID annonceId, String action) {
        return AnnonceStatus.getTargetStatusForAction(action)
            .map(targetStatus -> updateStatus(annonceId, targetStatus, action))
            .orElseThrow(() -> new ServiceException("Action inconnue: " + action));
    }

    private Annonce updateStatus(UUID annonceId, AnnonceStatus targetStatus, String action) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            Annonce annonce = em.find(Annonce.class, annonceId);
            if (annonce == null) {
                throw new ServiceException("Annonce non trouvée: " + annonceId);
            }
            annonce.setStatus(targetStatus);
            em.getTransaction().commit();
            return annonce;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de l'action " + action, e);
        }
    }

    public Annonce publish(UUID annonceId) {
        return changeStatus(annonceId, "publish");
    }

    public Annonce archive(UUID annonceId) {
        return changeStatus(annonceId, "archive");
    }

    public boolean delete(UUID annonceId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            Annonce annonce = em.find(Annonce.class, annonceId);
            if (annonce != null) {
                em.remove(annonce);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la suppression de l'annonce", e);
        }
    }

    public Optional<Annonce> findById(UUID id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return Optional.ofNullable(em.find(Annonce.class, id));
        }
    }

    public Optional<Annonce> findByIdWithRelations(UUID id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
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

    public List<Annonce> findAll(int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public List<Annonce> findAllPublished(int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class)
                    .setParameter(PARAM_STATUS, AnnonceStatus.PUBLISHED)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public List<Annonce> search(String keyword, int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE " +
                    "LOWER(a.title) LIKE LOWER(:keyword) OR " +
                    "LOWER(a.description) LIKE LOWER(:keyword) " +
                    "ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public List<Annonce> findByCategoryAndStatus(UUID categoryId, AnnonceStatus status, int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId AND a.status = :status ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class)
                    .setParameter("categoryId", categoryId)
                    .setParameter(PARAM_STATUS, status)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public long count() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT COUNT(a) FROM Annonce a", Long.class).getSingleResult();
        }
    }

    public long countPublished() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT COUNT(a) FROM Annonce a WHERE a.status = :status", Long.class)
                    .setParameter(PARAM_STATUS, AnnonceStatus.PUBLISHED)
                    .getSingleResult();
        }
    }

    public List<Annonce> findByAuthor(UUID authorId, int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.author.id = :authorId ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class)
                    .setParameter("authorId", authorId)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public long countByAuthor(UUID authorId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT COUNT(a) FROM Annonce a WHERE a.author.id = :authorId", Long.class)
                    .setParameter("authorId", authorId)
                    .getSingleResult();
        }
    }
}
