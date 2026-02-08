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
    private User defaultAuthor;
    private Category defaultCategory;

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
        defaultAuthor = userService.create(new User("author", "author@test.com", "password"));
        defaultCategory = categoryService.create(new Category("Immobilier"));
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

    private Annonce createAnnonce(String title, String description, String adress, String mail) {
        return annonceService.create(new Annonce(title, description, adress, mail), defaultAuthor.getId(), defaultCategory.getId());
    }

    @Test
    void create_shouldPersistAnnonce() {
        Annonce created = createAnnonce("Titre", "Description", "Adresse", "mail@test.com");

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(AnnonceStatus.DRAFT);
    }

    @Test
    void createWithRelations_shouldAssignAuthorAndCategory() {
        User author = userService.create(new User("author2", "author2@test.com", "password"));
        Category category = categoryService.create(new Category("Services"));
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");

        Annonce created = annonceService.create(annonce, author.getId(), category.getId());

        Optional<Annonce> found = annonceService.findByIdWithRelations(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getAuthor().getUsername()).isEqualTo("author2");
        assertThat(found.get().getCategory().getLabel()).isEqualTo("Services");
    }

    @Test
    void createWithInvalidAuthor_shouldThrowException() {
        UUID invalidAuthorId = UUID.randomUUID();
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");

        assertThatThrownBy(() -> annonceService.create(annonce, invalidAuthorId, defaultCategory.getId()))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("Auteur non trouve");
    }

    @Test
    void publish_shouldChangeStatusToPublished() {
        Annonce annonce = createAnnonce("Titre", "Description", "Adresse", "mail@test.com");
        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.DRAFT);

        Annonce published = annonceService.changeStatus(annonce.getId(), "publish");

        assertThat(published.getStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
    }

    @Test
    void archive_shouldChangeStatusToArchived() {
        Annonce annonce = createAnnonce("Titre", "Description", "Adresse", "mail@test.com");
        annonceService.changeStatus(annonce.getId(), "publish");

        Annonce archived = annonceService.changeStatus(annonce.getId(), "archive");

        assertThat(archived.getStatus()).isEqualTo(AnnonceStatus.ARCHIVED);
    }

    @Test
    void archiveFromDraft_shouldFail() {
        Annonce annonce = createAnnonce("Titre", "Description", "Adresse", "mail@test.com");

        assertThatThrownBy(() -> annonceService.changeStatus(annonce.getId(), "archive"))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Transition invalide");
    }

    @Test
    void delete_shouldRemoveAnnonce() {
        Annonce annonce = createAnnonce("Titre", "Description", "Adresse", "mail@test.com");

        boolean deleted = annonceService.delete(annonce.getId());

        assertThat(deleted).isTrue();
        assertThat(annonceService.findById(annonce.getId())).isEmpty();
    }

    @Test
    void findAllPublished_shouldReturnOnlyPublished() {
        createAnnonce("Draft", "Desc", "Addr", "mail@test.com");
        Annonce published = createAnnonce("Published", "Desc", "Addr", "mail2@test.com");
        annonceService.changeStatus(published.getId(), "publish");

        List<Annonce> result = annonceService.findAllPublished(0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Published");
    }

    @Test
    void search_shouldFindByKeyword() {
        createAnnonce("Voiture a vendre", "Belle voiture", "Paris", "mail@test.com");
        createAnnonce("Appartement", "Bel appartement", "Lyon", "mail2@test.com");

        List<Annonce> results = annonceService.search("voiture", 0, 10);

        assertThat(results).hasSize(1);
    }

    @Test
    void count_shouldReturnTotal() {
        createAnnonce("Titre 1", "Desc", "Addr", "mail@test.com");
        createAnnonce("Titre 2", "Desc", "Addr", "mail2@test.com");

        long count = annonceService.count();

        assertThat(count).isEqualTo(2);
    }
}
