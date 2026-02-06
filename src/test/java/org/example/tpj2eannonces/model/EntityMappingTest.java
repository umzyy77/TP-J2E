package org.example.tpj2eannonces.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

class EntityMappingTest {

    private EntityManager em;

    @BeforeAll
    static void setUpClass() {
        JPAUtil.getEntityManagerFactory();
    }

    @AfterAll
    static void tearDownClass() {
        JPAUtil.close();
    }

    @BeforeEach
    void setUp() {
        em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    @Test
    void user_shouldBePersisted() {
        User user = new User("testuser", "test@example.com", "password123");

        em.persist(user);
        em.flush();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void category_shouldBePersisted() {
        Category category = new Category("Immobilier");

        em.persist(category);
        em.flush();

        assertThat(category.getId()).isNotNull();
    }

    @Test
    void annonce_shouldBePersisted() {
        Annonce annonce = new Annonce("Titre test", "Description test", "123 rue Test", "test@example.com");

        em.persist(annonce);
        em.flush();

        assertThat(annonce.getId()).isNotNull();
        assertThat(annonce.getDate()).isNotNull();
        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.DRAFT);
    }

    @Test
    void annonce_shouldHaveAuthorRelation() {
        User author = new User("author", "author@example.com", "password123");
        em.persist(author);

        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");
        annonce.setAuthor(author);

        em.persist(annonce);
        em.flush();
        em.clear();

        Annonce found = em.find(Annonce.class, annonce.getId());
        assertThat(found.getAuthor()).isNotNull();
        assertThat(found.getAuthor().getUsername()).isEqualTo("author");
    }

    @Test
    void annonce_shouldHaveCategoryRelation() {
        Category category = new Category("Services");
        em.persist(category);

        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");
        annonce.setCategory(category);

        em.persist(annonce);
        em.flush();
        em.clear();

        Annonce found = em.find(Annonce.class, annonce.getId());
        assertThat(found.getCategory()).isNotNull();
        assertThat(found.getCategory().getLabel()).isEqualTo("Services");
    }

    @Test
    void annonce_publishShouldChangeStatus() {
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");
        assertThat(annonce.isDraft()).isTrue();

        annonce.publish();

        assertThat(annonce.isPublished()).isTrue();
        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
    }

    @Test
    void annonce_archiveShouldChangeStatus() {
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");
        annonce.publish();

        annonce.archive();

        assertThat(annonce.isArchived()).isTrue();
        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.ARCHIVED);
    }

    @Test
    void user_shouldHaveAnnoncesCollection() {
        User author = new User("testauthor", "testauthor@example.com", "password123");
        em.persist(author);

        Annonce annonce1 = new Annonce("Annonce 1", "Description 1", "Adresse 1", "mail1@test.com");
        annonce1.setAuthor(author);
        em.persist(annonce1);

        Annonce annonce2 = new Annonce("Annonce 2", "Description 2", "Adresse 2", "mail2@test.com");
        annonce2.setAuthor(author);
        em.persist(annonce2);

        em.flush();
        em.clear();

        User foundUser = em.find(User.class, author.getId());
        assertThat(foundUser.getAnnonces()).hasSize(2);
    }
}
