package org.example.tpj2eannonces.config;

import java.net.URL;

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
        initJaas();

        logger.info("Démarrage de l'application - initialisation de JPA...");
        JPAUtil.getEntityManagerFactory();
        logger.info("JPA initialisé avec succès.");
    }

    private void initJaas() {
        String existing = System.getProperty("java.security.auth.login.config");
        if (existing != null) {
            logger.info("Configuration JAAS deja definie via JVM: {}", existing);
            return;
        }

        URL jaasConfig = findJaasConfig();
        if (jaasConfig != null) {
            String configPath = jaasConfig.toExternalForm();
            System.setProperty("java.security.auth.login.config", configPath);
            logger.info("Configuration JAAS chargee: {}", configPath);
        } else {
            logger.warn("Fichier jaas.conf introuvable dans le classpath");
        }
    }

    protected URL findJaasConfig() {
        return getClass().getClassLoader().getResource("jaas.conf");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Arrêt de l'application - fermeture de JPA...");
        JPAUtil.close();
        logger.info("JPA fermé avec succès.");
    }
}
