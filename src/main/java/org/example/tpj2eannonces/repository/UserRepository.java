package org.example.tpj2eannonces.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class UserRepository extends GenericRepository<User, UUID> {

    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_EMAIL = "email";

    public UserRepository() {
        super(User.class);
    }

    public Optional<User> findByUsername(EntityManager em, String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter(PARAM_USERNAME, username);
        return query.getResultStream().findFirst();
    }

    public Optional<User> findByEmail(EntityManager em, String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter(PARAM_EMAIL, email);
        return query.getResultStream().findFirst();
    }

    public boolean existsByUsername(EntityManager em, String username) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter(PARAM_USERNAME, username)
                .getSingleResult();
        return count > 0;
    }

    public boolean existsByEmail(EntityManager em, String email) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter(PARAM_EMAIL, email)
                .getSingleResult();
        return count > 0;
    }

    public List<User> findAllOrderByCreatedAt(EntityManager em) {
        String jpql = "SELECT u FROM User u ORDER BY u.createdAt DESC";
        return em.createQuery(jpql, User.class).getResultList();
    }
}
