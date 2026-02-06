package org.example.tpj2eannonces.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {
    private static final Logger logger = LoggerFactory.getLogger(JPAUtil.class);
    private static final String PERSISTENCE_UNIT_NAME = "MasterAnnoncePU";

    private static EntityManagerFactory entityManagerFactory;

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            logger.info("Initialisation de l'EntityManagerFactory...");
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            logger.info("EntityManagerFactory initialisé avec succès.");
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static synchronized void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            logger.info("Fermeture de l'EntityManagerFactory...");
            entityManagerFactory.close();
            entityManagerFactory = null;
            logger.info("EntityManagerFactory fermé.");
        }
    }
}
