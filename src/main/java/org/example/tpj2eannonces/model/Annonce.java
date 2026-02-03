package org.example.tpj2eannonces.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Annonce {
    private UUID id;
    private String title;
    private String description;
    private String adress;
    private String mail;
    private LocalDateTime date;

    public Annonce() {
    }

    public Annonce(UUID id, String title, String description, String adress, String mail, LocalDateTime date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.adress = adress;
        this.mail = mail;
        this.date = date;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Date getDateAsDate() {
        if (date == null) {
            return null;
        }
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Annonce annonce = (Annonce) o;
        return Objects.equals(id, annonce.id)
                && Objects.equals(title, annonce.title)
                && Objects.equals(description, annonce.description)
                && Objects.equals(adress, annonce.adress)
                && Objects.equals(mail, annonce.mail)
                && Objects.equals(date, annonce.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, adress, mail, date);
    }

    @Override
    public String toString() {
        return "Annonce{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", adress='" + adress + '\'' +
                ", mail='" + mail + '\'' +
                ", date=" + date +
                '}';
    }
}
