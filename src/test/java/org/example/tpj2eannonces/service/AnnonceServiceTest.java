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
import org.example.tpj2eannonces.exception.ForbiddenException;
import org.example.tpj2eannonces.exception.NotFoundException;
import org.example.tpj2eannonces.exception.annonce.AnnonceImmutableException;
import org.example.tpj2eannonces.exception.annonce.ArchiveRequiredException;
import org.example.tpj2eannonces.exception.annonce.InvalidTransitionException;
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
        Long categoryId = defaultCategory.getId();

        assertThatThrownBy(() -> annonceService.create(annonce, invalidAuthorId, categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Auteur non trouve");
    }

    @Test
    void publish_shouldChangeStatusToPublished() {
        Annonce annonce = createAnnonce("Titre", "Description", "Adresse", "mail@test.com");
        assertThat(annonce.getStatus()).isEqualTo(AnnonceStatus.DRAFT);

        Annonce published = annonceService.changeStatus(annonce.getId(), defaultAuthor.getId(), "publish");

        assertThat(published.getStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
    }

    @Test
    void archive_shouldChangeStatusToArchived() {
        Annonce annonce = createAnnonce("Titre", "Description", "Adresse", "mail@test.com");
        annonceService.changeStatus(annonce.getId(), defaultAuthor.getId(), "publish");

        Annonce archived = annonceService.changeStatus(annonce.getId(), defaultAuthor.getId(), "archive");

        assertThat(archived.getStatus()).isEqualTo(AnnonceStatus.ARCHIVED);
    }

    @Test
    void archiveFromDraft_shouldFail() {
        Annonce annonce = createAnnonce("Titre", "Description", "Adresse", "mail@test.com");
        Long annonceId = annonce.getId();

        UUID authorId = defaultAuthor.getId();
        assertThatThrownBy(() -> annonceService.changeStatus(annonceId, authorId, "archive"))
                .isInstanceOf(InvalidTransitionException.class)
                .hasMessageContaining("Transition invalide");
    }

    @Test
    void delete_shouldRemoveAnnonce() {
        Annonce annonce = createAnnonce("Titre", "Description", "Adresse", "mail@test.com");

        annonceService.changeStatus(annonce.getId(), defaultAuthor.getId(), "publish");
        annonceService.changeStatus(annonce.getId(), defaultAuthor.getId(), "archive");
        boolean deleted = annonceService.delete(annonce.getId(), defaultAuthor.getId());

        assertThat(deleted).isTrue();
        assertThat(annonceService.findById(annonce.getId())).isEmpty();
    }

    @Test
    void findAllPublished_shouldReturnOnlyPublished() {
        createAnnonce("Draft", "Desc", "Addr", "mail@test.com");
        Annonce published = createAnnonce("Published", "Desc", "Addr", "mail2@test.com");
        annonceService.changeStatus(published.getId(), defaultAuthor.getId(), "publish");

        List<Annonce> result = annonceService.findAllPublished(0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Published");
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

    @Test
    void updateFields_shouldUpdateDraftAnnonce() {
        Annonce annonce = createAnnonce("Old", "Old desc", "Old addr", "old@test.com");
        Category newCat = categoryService.create(new Category("Services"));

        Annonce updated = annonceService.updateFields(
                annonce.getId(), defaultAuthor.getId(),
                "New", "New desc", "New addr", "new@test.com", newCat.getId());

        assertThat(updated.getTitle()).isEqualTo("New");
        assertThat(updated.getDescription()).isEqualTo("New desc");
    }

    @Test
    void updateFields_shouldRejectNonOwner() {
        Annonce annonce = createAnnonce("Titre", "Desc", "Addr", "mail@test.com");
        UUID otherUserId = UUID.randomUUID();
        Long annonceId = annonce.getId();
        Long categoryId = defaultCategory.getId();

        assertThatThrownBy(() -> annonceService.updateFields(
                annonceId, otherUserId, "New", "Desc", "Addr", "mail@test.com", categoryId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateFields_shouldRejectPublishedAnnonce() {
        Annonce annonce = createAnnonce("Titre", "Desc", "Addr", "mail@test.com");
        annonceService.changeStatus(annonce.getId(), defaultAuthor.getId(), "publish");
        Long annonceId = annonce.getId();
        UUID authorId = defaultAuthor.getId();
        Long catId = defaultCategory.getId();

        assertThatThrownBy(() -> annonceService.updateFields(
                annonceId, authorId, "New", "Desc", "Addr", "mail@test.com", catId))
                .isInstanceOf(AnnonceImmutableException.class);
    }

    @Test
    void delete_shouldRejectNonOwner() {
        Annonce annonce = createAnnonce("Titre", "Desc", "Addr", "mail@test.com");
        annonceService.changeStatus(annonce.getId(), defaultAuthor.getId(), "publish");
        annonceService.changeStatus(annonce.getId(), defaultAuthor.getId(), "archive");
        UUID otherUserId = UUID.randomUUID();
        Long annonceId = annonce.getId();

        assertThatThrownBy(() -> annonceService.delete(annonceId, otherUserId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void delete_shouldRejectNonArchivedAnnonce() {
        Annonce annonce = createAnnonce("Titre", "Desc", "Addr", "mail@test.com");
        Long annonceId = annonce.getId();
        UUID authorId = defaultAuthor.getId();

        assertThatThrownBy(() -> annonceService.delete(annonceId, authorId))
                .isInstanceOf(ArchiveRequiredException.class);
    }

    @Test
    void findByIdWithRelations_shouldReturnAnnonceWithRelations() {
        Annonce annonce = createAnnonce("Titre", "Desc", "Addr", "mail@test.com");

        Optional<Annonce> found = annonceService.findByIdWithRelations(annonce.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getAuthor()).isNotNull();
        assertThat(found.get().getCategory()).isNotNull();
    }

    @Test
    void findByIdWithRelations_shouldReturnEmptyForNonexistent() {
        Optional<Annonce> found = annonceService.findByIdWithRelations(99999L);

        assertThat(found).isEmpty();
    }

    @Test
    void countPublished_shouldReturnOnlyPublished() {
        createAnnonce("Draft", "Desc", "Addr", "mail@test.com");
        Annonce pub = createAnnonce("Published", "Desc", "Addr", "mail2@test.com");
        annonceService.changeStatus(pub.getId(), defaultAuthor.getId(), "publish");

        assertThat(annonceService.countPublished()).isEqualTo(1);
    }

    @Test
    void findByAuthor_shouldReturnAuthorAnnonces() {
        createAnnonce("T1", "D", "A", "m1@test.com");
        createAnnonce("T2", "D", "A", "m2@test.com");

        List<Annonce> result = annonceService.findByAuthor(defaultAuthor.getId(), 0, 10);

        assertThat(result).hasSize(2);
    }

    @Test
    void countByAuthor_shouldReturnCount() {
        createAnnonce("T1", "D", "A", "m1@test.com");

        long count = annonceService.countByAuthor(defaultAuthor.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    void findByFilters_shouldFilterByCategory() {
        Category cat2 = categoryService.create(new Category("Auto"));
        createAnnonce("T1", "D", "A", "m1@test.com");
        annonceService.create(new Annonce("T2", "D", "A", "m2@test.com"), defaultAuthor.getId(), cat2.getId());

        List<Annonce> result = annonceService.findByFilters(defaultCategory.getId(), null, 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByFilters_shouldFilterByStatus() {
        createAnnonce("Draft", "D", "A", "m1@test.com");
        Annonce pub = createAnnonce("Pub", "D", "A", "m2@test.com");
        annonceService.changeStatus(pub.getId(), defaultAuthor.getId(), "publish");

        List<Annonce> result = annonceService.findByFilters(null, AnnonceStatus.PUBLISHED, 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void countByFilters_shouldReturnFilteredCount() {
        createAnnonce("T1", "D", "A", "m1@test.com");
        createAnnonce("T2", "D", "A", "m2@test.com");

        long count = annonceService.countByFilters(null, AnnonceStatus.DRAFT);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void searchByFilters_shouldCombineKeywordAndFilters() {
        createAnnonce("Voiture rouge", "Belle voiture", "Paris", "m1@test.com");
        createAnnonce("Appartement", "Bel appart", "Lyon", "m2@test.com");

        List<Annonce> result = annonceService.searchByFilters("voiture", null, null, 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void countBySearchAndFilters_shouldReturnKeywordCount() {
        createAnnonce("Voiture", "Desc", "Addr", "m1@test.com");
        createAnnonce("Appart", "Desc", "Addr", "m2@test.com");

        long count = annonceService.countBySearchAndFilters("voiture", null, null);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByKeyword_shouldDelegateToCountBySearchAndFilters() {
        createAnnonce("Voiture", "Desc", "Addr", "m@test.com");

        long count = annonceService.countByKeyword("voiture");

        assertThat(count).isEqualTo(1);
    }

    @Test
    void findAll_shouldReturnPaginated() {
        for (int i = 0; i < 15; i++) {
            createAnnonce("T" + i, "D", "A", "m" + i + "@test.com");
        }

        List<Annonce> page0 = annonceService.findAll(0, 5);
        List<Annonce> page1 = annonceService.findAll(1, 5);

        assertThat(page0).hasSize(5);
        assertThat(page1).hasSize(5);
    }

    @Test
    void changeStatus_shouldRejectNullOwner() {
        Annonce annonce = createAnnonce("Titre", "Desc", "Addr", "mail@test.com");
        Long annonceId = annonce.getId();

        assertThatThrownBy(() -> annonceService.changeStatus(annonceId, null, "publish"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateFields_shouldWorkWithNullCategoryId() {
        Annonce annonce = createAnnonce("Titre", "Desc", "Addr", "mail@test.com");

        Annonce updated = annonceService.updateFields(
                annonce.getId(), defaultAuthor.getId(),
                "New", "New desc", "New addr", "new@test.com", null);

        assertThat(updated.getTitle()).isEqualTo("New");
    }

    @Test
    void changeStatus_shouldRejectUnknownAction() {
        Annonce annonce = createAnnonce("Titre", "Desc", "Addr", "mail@test.com");
        Long annonceId = annonce.getId();
        UUID ownerId = defaultAuthor.getId();

        assertThatThrownBy(() -> annonceService.changeStatus(annonceId, ownerId, "foobar"))
                .isInstanceOf(InvalidTransitionException.class)
                .hasMessageContaining("Action inconnue");
    }

    @Test
    void updateFields_shouldRejectInvalidCategoryId() {
        Annonce annonce = createAnnonce("Titre", "Desc", "Addr", "mail@test.com");
        Long annonceId = annonce.getId();
        UUID ownerId = defaultAuthor.getId();

        assertThatThrownBy(() -> annonceService.updateFields(
                annonceId, ownerId, "New", "Desc", "Addr", "mail@test.com", 99999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Categorie non trouvee");
    }

    @Test
    void delete_shouldRejectNonexistentAnnonce() {
        UUID ownerId = defaultAuthor.getId();

        assertThatThrownBy(() -> annonceService.delete(99999L, ownerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Annonce non trouvee");
    }

    @Test
    void updateFields_shouldRejectNonexistentAnnonce() {
        UUID ownerId = defaultAuthor.getId();
        Long categoryId = defaultCategory.getId();

        assertThatThrownBy(() -> annonceService.updateFields(
                99999L, ownerId, "New", "Desc", "Addr", "mail@test.com", categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Annonce non trouvee");
    }
}
