package org.example.tpj2eannonces.utils;

import org.example.tpj2eannonces.config.DatabaseConfig;
import org.example.tpj2eannonces.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("java:S6548") // Singleton intentionnel pour la gestion centralisée des connexions DB
public class ConnectionDB {
    private final DatabaseConfig config;

    private ConnectionDB() {
        this.config = new DatabaseConfig();
    }

    private static class Holder {
        private static final ConnectionDB INSTANCE = new ConnectionDB();
    }

    public static ConnectionDB getInstance() {
        return Holder.INSTANCE;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
        } catch (SQLException e) {
            throw new DatabaseException("Impossible d'etablir la connexion a la base", e);
        }
    }
}
