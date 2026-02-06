package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.example.tpj2eannonces.utils.PasswordUtils;

import jakarta.persistence.EntityManager;

public class UserService {

    public User create(User user) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();

            Long usernameCount = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", user.getUsername())
                    .getSingleResult();
            if (usernameCount > 0) {
                throw new ServiceException("Le nom d'utilisateur existe déjà: " + user.getUsername());
            }

            Long emailCount = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", user.getEmail())
                    .getSingleResult();
            if (emailCount > 0) {
                throw new ServiceException("L'email existe déjà: " + user.getEmail());
            }

            user.setPassword(PasswordUtils.hash(user.getPassword()));

            em.persist(user);
            em.getTransaction().commit();
            return user;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la création de l'utilisateur", e);
        }
    }

    public User update(User user) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            User merged = em.merge(user);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la mise à jour de l'utilisateur", e);
        }
    }

    public boolean delete(UUID userId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                em.remove(user);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la suppression de l'utilisateur", e);
        }
    }

    public Optional<User> findById(UUID id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return Optional.ofNullable(em.find(User.class, id));
        }
    }

    public Optional<User> findByUsername(String username) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
        }
    }

    public Optional<User> authenticate(String username, String password) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Optional<User> userOpt = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
            
            if (userOpt.isPresent() && PasswordUtils.verify(password, userOpt.get().getPassword())) {
                return userOpt;
            }
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT u FROM User u ORDER BY u.createdAt DESC", User.class)
                    .getResultList();
        }
    }
}
