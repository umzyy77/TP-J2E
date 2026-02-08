package org.example.tpj2eannonces.utils;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {
    private static final Logger logger = LoggerFactory.getLogger(JPAUtil.class);
    private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "MasterAnnoncePU";
    private static final String PERSISTENCE_UNIT_PROPERTY = "tpj2e.persistence.unit";

    private static EntityManagerFactory entityManagerFactory;

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            String persistenceUnitName = resolvePersistenceUnitName();
            logger.info("Initialisation de l'EntityManagerFactory...");
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
            logger.info("EntityManagerFactory initialise avec succes.");
        }
        return entityManagerFactory;
    }

    private static String resolvePersistenceUnitName() {
        return System.getProperty(PERSISTENCE_UNIT_PROPERTY, DEFAULT_PERSISTENCE_UNIT_NAME);
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static <T> T inTransaction(Function<EntityManager, T> action) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T result = action.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public static <T> T inReadOnly(Function<EntityManager, T> action) {
        try (EntityManager em = getEntityManager()) {
            return action.apply(em);
        }
    }

    public static synchronized void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            logger.info("Fermeture de l'EntityManagerFactory...");
            entityManagerFactory.close();
            entityManagerFactory = null;
            logger.info("EntityManagerFactory ferme.");
        }
    }
}
