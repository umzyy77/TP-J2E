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

    public Annonce create(Annonce annonce) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(annonce);
            em.getTransaction().commit();
            return annonce;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServiceException("Erreur lors de la création de l'annonce", e);
        } finally {
            em.close();
        }
    }

    public Annonce create(Annonce annonce, UUID authorId, UUID categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServiceException("Erreur lors de la création de l'annonce", e);
        } finally {
            em.close();
        }
    }

    public Annonce update(Annonce annonce) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Annonce merged = em.merge(annonce);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServiceException("Erreur lors de la mise à jour de l'annonce", e);
        } finally {
            em.close();
        }
    }

    public Annonce publish(UUID annonceId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Annonce annonce = em.find(Annonce.class, annonceId);
            if (annonce == null) {
                throw new ServiceException("Annonce non trouvée: " + annonceId);
            }
            annonce.publish();
            em.getTransaction().commit();
            return annonce;
        } catch (ServiceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServiceException("Erreur lors de la publication de l'annonce", e);
        } finally {
            em.close();
        }
    }

    public Annonce archive(UUID annonceId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Annonce annonce = em.find(Annonce.class, annonceId);
            if (annonce == null) {
                throw new ServiceException("Annonce non trouvée: " + annonceId);
            }
            annonce.archive();
            em.getTransaction().commit();
            return annonce;
        } catch (ServiceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServiceException("Erreur lors de l'archivage de l'annonce", e);
        } finally {
            em.close();
        }
    }

    public boolean delete(UUID annonceId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServiceException("Erreur lors de la suppression de l'annonce", e);
        } finally {
            em.close();
        }
    }

    public Optional<Annonce> findById(UUID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Annonce annonce = em.find(Annonce.class, id);
            return Optional.ofNullable(annonce);
        } finally {
            em.close();
        }
    }

    public Optional<Annonce> findByIdWithRelations(UUID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a " +
                    "LEFT JOIN FETCH a.author " +
                    "LEFT JOIN FETCH a.category " +
                    "WHERE a.id = :id";
            return em.createQuery(jpql, Annonce.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findAll(int page, int size) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findAllPublished(int page, int size) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class)
                    .setParameter("status", AnnonceStatus.PUBLISHED)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> search(String keyword, int page, int size) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE " +
                    "LOWER(a.title) LIKE LOWER(:keyword) OR " +
                    "LOWER(a.description) LIKE LOWER(:keyword) " +
                    "ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByCategoryAndStatus(UUID categoryId, AnnonceStatus status, int page, int size) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT a FROM Annonce a WHERE a.category.id = :categoryId AND a.status = :status ORDER BY a.date DESC";
            return em.createQuery(jpql, Annonce.class)
                    .setParameter("categoryId", categoryId)
                    .setParameter("status", status)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(a) FROM Annonce a", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public long countPublished() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(a) FROM Annonce a WHERE a.status = :status", Long.class)
                    .setParameter("status", AnnonceStatus.PUBLISHED)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
