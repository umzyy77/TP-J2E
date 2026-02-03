package org.example.tpj2eannonces.dao;

import org.example.tpj2eannonces.exception.DatabaseException;
import org.example.tpj2eannonces.mapper.AnnonceMapper;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.utils.ConnectionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AnnonceDAO extends DAO<Annonce> {
    private final ConnectionDB connectionDB;
    private final AnnonceMapper mapper;

    public AnnonceDAO() {
        this.connectionDB = ConnectionDB.getInstance();
        this.mapper = new AnnonceMapper();
    }

    @Override
    public Annonce create(Annonce annonce) {
        if (annonce == null) {
            return null;
        }

        String sql = "INSERT INTO annonce (title, description, adress, mail, date) VALUES (?, ?, ?, ?, ?) RETURNING id";
        LocalDateTime date = annonce.getDate() == null ? LocalDateTime.now() : annonce.getDate();

        try (Connection connection = connectionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, annonce.getTitle());
            statement.setString(2, annonce.getDescription());
            statement.setString(3, annonce.getAdress());
            statement.setString(4, annonce.getMail());
            statement.setTimestamp(5, Timestamp.valueOf(date));

            try (ResultSet keys = statement.executeQuery()) {
                if (keys.next()) {
                    UUID id = keys.getObject(1, UUID.class);
                    annonce.setId(id);
                } else {
                    return null;
                }
            }

            annonce.setDate(date);
            return annonce;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur creation annonce", e);
        }
    }

    @Override
    public Annonce update(Annonce annonce) {
        if (annonce == null || annonce.getId() == null) {
            return null;
        }

        String sql = "UPDATE annonce SET title = ?, description = ?, adress = ?, mail = ? WHERE id = ?";

        try (Connection connection = connectionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, annonce.getTitle());
            statement.setString(2, annonce.getDescription());
            statement.setString(3, annonce.getAdress());
            statement.setString(4, annonce.getMail());
            statement.setObject(5, annonce.getId());

            int updated = statement.executeUpdate();
            return updated > 0 ? annonce : null;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur mise a jour annonce", e);
        }
    }

    @Override
    public boolean delete(UUID id) {
        String sql = "DELETE FROM annonce WHERE id = ?";

        try (Connection connection = connectionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur suppression annonce", e);
        }
    }

    @Override
    public Annonce find(UUID id) {
        String sql = "SELECT id, title, description, adress, mail, date FROM annonce WHERE id = ?";

        try (Connection connection = connectionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapper.map(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur recherche annonce", e);
        }
    }

    @Override
    public List<Annonce> list() {
        String sql = "SELECT id, title, description, adress, mail, date FROM annonce ORDER BY date DESC";
        List<Annonce> annonces = new ArrayList<>();

        try (Connection connection = connectionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                annonces.add(mapper.map(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur liste annonces", e);
        }

        return annonces;
    }
}
