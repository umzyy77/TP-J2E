package org.example.tpj2eannonces.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

class AnnonceRepositoryTest {

    private AnnonceRepository repository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
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
        repository = new AnnonceRepository();
        userRepository = new UserRepository();
        categoryRepository = new CategoryRepository();
        cleanDatabase();
        defaultAuthor = saveUser("defaultUser", "default@test.com");
        defaultCategory = saveCategory("DefaultCategory");
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

    private Annonce saveAnnonce(String title, String description, String adress, String mail) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            Annonce annonce = new Annonce(title, description, adress, mail);
            annonce.setAuthor(em.find(User.class, defaultAuthor.getId()));
            annonce.setCategory(em.find(Category.class, defaultCategory.getId()));
            repository.save(em, annonce);
            em.getTransaction().commit();
            return annonce;
        }
    }

    private User saveUser(String username, String email) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            User user = new User(username, email, "password");
            userRepository.save(em, user);
            em.getTransaction().commit();
            return user;
        }
    }

    private Category saveCategory(String label) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            Category category = new Category(label);
            categoryRepository.save(em, category);
            em.getTransaction().commit();
            return category;
        }
    }

    @Test
    void save_shouldPersistAnnonce() {
        Annonce saved = saveAnnonce("Titre", "Description", "Adresse", "mail@test.com");

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(AnnonceStatus.DRAFT);
    }

    @Test
    void findById_shouldReturnAnnonce() {
        Annonce annonce = saveAnnonce("Titre", "Description", "Adresse", "mail@test.com");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            Optional<Annonce> found = repository.findById(em, annonce.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("Titre");
        }
    }

    @Test
    void update_shouldModifyAnnonce() {
        Annonce annonce = saveAnnonce("Titre", "Description", "Adresse", "mail@test.com");
        annonce.setTitle("Nouveau Titre");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            Annonce updated = repository.update(em, annonce);
            em.getTransaction().commit();
            assertThat(updated.getTitle()).isEqualTo("Nouveau Titre");
        }
    }

    @Test
    void deleteById_shouldRemoveAnnonce() {
        Annonce annonce = saveAnnonce("Titre", "Description", "Adresse", "mail@test.com");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            boolean deleted = repository.deleteById(em, annonce.getId());
            em.getTransaction().commit();
            assertThat(deleted).isTrue();
        }

        try (EntityManager em2 = JPAUtil.getEntityManager()) {
            assertThat(repository.findById(em2, annonce.getId())).isEmpty();
        }
    }

    @Test
    void findAll_shouldReturnAllAnnonces() {
        saveAnnonce("Titre 1", "Desc 1", "Addr 1", "mail1@test.com");
        saveAnnonce("Titre 2", "Desc 2", "Addr 2", "mail2@test.com");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Annonce> all = repository.findAllWithRelations(em);
            assertThat(all).hasSize(2);
        }
    }

    @Test
    void findAll_withPagination_shouldReturnPage() {
        for (int i = 0; i < 15; i++) {
            saveAnnonce("Titre " + i, "Desc " + i, "Addr " + i, "mail" + i + "@test.com");
        }

        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Annonce> page0 = repository.findAllWithRelations(em, 0, 5);
            List<Annonce> page1 = repository.findAllWithRelations(em, 1, 5);
            assertThat(page0).hasSize(5);
            assertThat(page1).hasSize(5);
        }
    }

    @Test
    void searchByKeyword_shouldFindByTitle() {
        saveAnnonce("Voiture a vendre", "Belle voiture", "Paris", "mail@test.com");
        saveAnnonce("Appartement", "Bel appartement", "Lyon", "mail2@test.com");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Annonce> results = repository.searchByKeyword(em, "voiture", 0, 100);
            assertThat(results).hasSize(1);
            assertThat(results.getFirst().getTitle()).contains("Voiture");
        }
    }

    @Test
    void searchByKeyword_shouldFindByDescription() {
        saveAnnonce("A vendre", "Belle voiture occasion", "Paris", "mail@test.com");
        saveAnnonce("Appartement", "Bel appartement", "Lyon", "mail2@test.com");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Annonce> results = repository.searchByKeyword(em, "voiture", 0, 100);
            assertThat(results).hasSize(1);
        }
    }

    @Test
    void findByStatus_shouldFilterByStatus() {
        saveAnnonce("Draft", "Desc", "Addr", "mail@test.com");
        Annonce published = saveAnnonce("Published", "Desc", "Addr", "mail2@test.com");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            repository.updateStatus(em, published.getId(), AnnonceStatus.PUBLISHED);
            em.getTransaction().commit();
        }

        try (EntityManager em2 = JPAUtil.getEntityManager()) {
            List<Annonce> drafts = repository.findByStatus(em2, AnnonceStatus.DRAFT, 0, 100);
            List<Annonce> publishedList = repository.findByStatus(em2, AnnonceStatus.PUBLISHED, 0, 100);
            assertThat(drafts).hasSize(1);
            assertThat(publishedList).hasSize(1);
        }
    }

    @Test
    void findByAuthor_shouldFilterByAuthor() {
        User author1 = saveUser("user1", "user1@test.com");
        User author2 = saveUser("user2", "user2@test.com");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            Category managedCategory = em.find(Category.class, defaultCategory.getId());

            Annonce a1 = new Annonce("Annonce 1", "Desc", "Addr", "mail@test.com");
            a1.setAuthor(em.find(User.class, author1.getId()));
            a1.setCategory(managedCategory);
            repository.save(em, a1);

            Annonce a2 = new Annonce("Annonce 2", "Desc", "Addr", "mail2@test.com");
            a2.setAuthor(em.find(User.class, author2.getId()));
            a2.setCategory(managedCategory);
            repository.save(em, a2);
            em.getTransaction().commit();
        }

        try (EntityManager em2 = JPAUtil.getEntityManager()) {
            List<Annonce> byAuthor1 = repository.findByAuthor(em2, author1.getId(), 0, 100);
            assertThat(byAuthor1).hasSize(1);
            assertThat(byAuthor1.getFirst().getTitle()).isEqualTo("Annonce 1");
        }
    }

    @Test
    void countByStatus_shouldReturnCorrectCount() {
        saveAnnonce("Draft 1", "Desc", "Addr", "mail@test.com");
        saveAnnonce("Draft 2", "Desc", "Addr", "mail2@test.com");
        Annonce published = saveAnnonce("Published", "Desc", "Addr", "mail3@test.com");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            repository.updateStatus(em, published.getId(), AnnonceStatus.PUBLISHED);
            em.getTransaction().commit();
        }

        try (EntityManager em2 = JPAUtil.getEntityManager()) {
            long draftCount = repository.countByStatus(em2, AnnonceStatus.DRAFT);
            long publishedCount = repository.countByStatus(em2, AnnonceStatus.PUBLISHED);
            assertThat(draftCount).isEqualTo(2);
            assertThat(publishedCount).isEqualTo(1);
        }
    }

    @Test
    void count_shouldReturnTotalCount() {
        saveAnnonce("Titre 1", "Desc", "Addr", "mail@test.com");
        saveAnnonce("Titre 2", "Desc", "Addr", "mail2@test.com");

        try (EntityManager em = JPAUtil.getEntityManager()) {
            long count = repository.count(em);
            assertThat(count).isEqualTo(2);
        }
    }
}
