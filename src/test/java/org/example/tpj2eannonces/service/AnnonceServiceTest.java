package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.repository.RepositoryException;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

class AnnonceServiceTest {

    private AnnonceService annonceService;
    private UserService userService;
    private CategoryService categoryService;

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
        annonceService = new AnnonceService();
        userService = new UserService();
        categoryService = new CategoryService();
        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Annonce").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Category").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    void create_shouldPersistAnnonce() {
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");

        Annonce created = annonceService.create(annonce, null, null);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(AnnonceStatus.DRAFT);
    }

    @Test
    void createWithRelations_shouldAssignAuthorAndCategory() {
        User author = userService.create(new User("author", "author@test.com", "password"));
        Category category = categoryService.create(new Category("Immobilier"));
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");

        Annonce created = annonceService.create(annonce, author.getId(), category.getId());

        Optional<Annonce> found = annonceService.findByIdWithRelations(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getAuthor().getUsername()).isEqualTo("author");
        assertThat(found.get().getCategory().getLabel()).isEqualTo("Immobilier");
    }

    @Test
    void createWithInvalidAuthor_shouldThrowException() {
        UUID invalidAuthorId = UUID.randomUUID();
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");

        assertThatThrownBy(() -> annonceService.create(annonce, invalidAuthorId, null))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Auteur non trouvé");
    }

    @Test
    void publish_shouldChangeStatusToPublished() {
        Annonce annonce = annonceService.create(new Annonce("Titre", "Description", "Adresse", "mail@test.com"), null, null);
        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.DRAFT);

        Annonce published = annonceService.changeStatus(annonce.getId(), "publish");

        assertThat(published.getStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
    }

    @Test
    void archive_shouldChangeStatusToArchived() {
        Annonce annonce = annonceService.create(new Annonce("Titre", "Description", "Adresse", "mail@test.com"), null, null);
        annonceService.changeStatus(annonce.getId(), "publish");

        Annonce archived = annonceService.changeStatus(annonce.getId(), "archive");

        assertThat(archived.getStatus()).isEqualTo(AnnonceStatus.ARCHIVED);
    }

    @Test
    void delete_shouldRemoveAnnonce() {
        Annonce annonce = annonceService.create(new Annonce("Titre", "Description", "Adresse", "mail@test.com"), null, null);

        boolean deleted = annonceService.delete(annonce.getId());

        assertThat(deleted).isTrue();
        assertThat(annonceService.findById(annonce.getId())).isEmpty();
    }

    @Test
    void findAllPublished_shouldReturnOnlyPublished() {
        annonceService.create(new Annonce("Draft", "Desc", "Addr", "mail@test.com"), null, null);
        Annonce published = annonceService.create(new Annonce("Published", "Desc", "Addr", "mail2@test.com"), null, null);
        annonceService.changeStatus(published.getId(), "publish");

        List<Annonce> result = annonceService.findAllPublished(0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Published");
    }

    @Test
    void search_shouldFindByKeyword() {
        annonceService.create(new Annonce("Voiture à vendre", "Belle voiture", "Paris", "mail@test.com"), null, null);
        annonceService.create(new Annonce("Appartement", "Bel appartement", "Lyon", "mail2@test.com"), null, null);

        List<Annonce> results = annonceService.search("voiture", 0, 10);

        assertThat(results).hasSize(1);
    }

    @Test
    void count_shouldReturnTotal() {
        annonceService.create(new Annonce("Titre 1", "Desc", "Addr", "mail@test.com"), null, null);
        annonceService.create(new Annonce("Titre 2", "Desc", "Addr", "mail2@test.com"), null, null);

        long count = annonceService.count();

        assertThat(count).isEqualTo(2);
    }
}
