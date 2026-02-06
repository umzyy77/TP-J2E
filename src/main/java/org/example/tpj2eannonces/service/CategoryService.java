package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.utils.JPAUtil;

import jakarta.persistence.EntityManager;

public class CategoryService {

    public Category create(Category category) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Long count = em.createQuery("SELECT COUNT(c) FROM Category c WHERE c.label = :label", Long.class)
                    .setParameter("label", category.getLabel())
                    .getSingleResult();
            if (count > 0) {
                throw new ServiceException("La catégorie existe déjà: " + category.getLabel());
            }

            em.persist(category);
            em.getTransaction().commit();
            return category;
        } catch (ServiceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServiceException("Erreur lors de la création de la catégorie", e);
        } finally {
            em.close();
        }
    }

    public Category update(Category category) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Category merged = em.merge(category);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServiceException("Erreur lors de la mise à jour de la catégorie", e);
        } finally {
            em.close();
        }
    }

    public boolean delete(UUID categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Category category = em.find(Category.class, categoryId);
            if (category != null) {
                Long count = em.createQuery("SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :id", Long.class)
                        .setParameter("id", categoryId)
                        .getSingleResult();
                if (count > 0) {
                    throw new ServiceException("Impossible de supprimer: " + count + " annonce(s) liée(s)");
                }
                em.remove(category);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (ServiceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServiceException("Erreur lors de la suppression de la catégorie", e);
        } finally {
            em.close();
        }
    }

    public Optional<Category> findById(UUID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Category.class, id));
        } finally {
            em.close();
        }
    }

    public Optional<Category> findByLabel(String label) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Category c WHERE c.label = :label", Category.class)
                    .setParameter("label", label)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    public List<Category> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Category c ORDER BY c.label", Category.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
