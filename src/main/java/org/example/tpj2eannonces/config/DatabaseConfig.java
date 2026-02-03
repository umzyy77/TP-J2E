package org.example.tpj2eannonces.config;

import org.example.tpj2eannonces.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String CONFIG_FILE = "application.properties";

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig() {
        Properties properties = new Properties();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new DatabaseException("Fichier de configuration introuvable: " + CONFIG_FILE);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new DatabaseException("Erreur de lecture du fichier de configuration", e);
        }

        this.url = getRequired(properties, "db.url");
        this.user = getRequired(properties, "db.user");
        this.password = getRequired(properties, "db.password");

        if (logger.isInfoEnabled()) {
            logger.info("Configuration BDD chargee : {}", sanitizeUrl(url));
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private String getRequired(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new DatabaseException("Parametre manquant dans application.properties: " + key);
        }
        return value.trim();
    }

    private static final String CREDENTIAL_PARAM = "password";
    private static final String MASKED_VALUE = "***";

    private String sanitizeUrl(String url) {
        return url.replaceAll(CREDENTIAL_PARAM + "=[^&]+", CREDENTIAL_PARAM + "=" + MASKED_VALUE);
    }
}
