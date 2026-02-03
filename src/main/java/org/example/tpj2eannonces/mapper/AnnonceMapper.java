package org.example.tpj2eannonces.mapper;

import org.example.tpj2eannonces.exception.DatabaseException;
import org.example.tpj2eannonces.model.Annonce;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class AnnonceMapper {
    public Annonce map(ResultSet resultSet) {
        try {
            UUID id = resultSet.getObject("id", UUID.class);
            String title = resultSet.getString("title");
            String description = resultSet.getString("description");
            String adress = resultSet.getString("adress");
            String mail = resultSet.getString("mail");
            Timestamp timestamp = resultSet.getTimestamp("date");
            LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;
            return new Annonce(id, title, description, adress, mail, date);
        } catch (SQLException e) {
            throw new DatabaseException("Erreur de mapping annonce", e);
        }
    }
}
