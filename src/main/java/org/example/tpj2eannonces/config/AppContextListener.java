package org.example.tpj2eannonces.config;

import org.example.tpj2eannonces.utils.JPAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Démarrage de l'application - initialisation de JPA...");
        JPAUtil.getEntityManagerFactory();
        logger.info("JPA initialisé avec succès.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Arrêt de l'application - fermeture de JPA...");
        JPAUtil.close();
        logger.info("JPA fermé avec succès.");
    }
}
