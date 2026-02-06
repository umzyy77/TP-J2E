package org.example.tpj2eannonces.repository;

import java.util.Optional;

import org.example.tpj2eannonces.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class UserRepository extends GenericRepository<User> {

    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_PASSWORD = "password";

    public UserRepository() {
        super(User.class);
    }

    public Optional<User> findByUsername(String username) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT u FROM User u WHERE u.username = :username";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter(PARAM_USERNAME, username);
            return query.getResultStream().findFirst();
        }
    }

    public Optional<User> findByEmail(String email) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT u FROM User u WHERE u.email = :email";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter(PARAM_EMAIL, email);
            return query.getResultStream().findFirst();
        }
    }

    public boolean existsByUsername(String username) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter(PARAM_USERNAME, username)
                    .getSingleResult();
            return count > 0;
        }
    }

    public boolean existsByEmail(String email) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter(PARAM_EMAIL, email)
                    .getSingleResult();
            return count > 0;
        }
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter(PARAM_USERNAME, username);
            query.setParameter(PARAM_PASSWORD, password);
            return query.getResultStream().findFirst();
        }
    }
}
