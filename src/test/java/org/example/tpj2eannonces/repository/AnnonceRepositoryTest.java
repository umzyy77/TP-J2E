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
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Annonce").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Category").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    void save_shouldPersistAnnonce() {
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");

        Annonce saved = repository.save(annonce);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(AnnonceStatus.DRAFT);
    }

    @Test
    void findById_shouldReturnAnnonce() {
        Annonce annonce = repository.save(new Annonce("Titre", "Description", "Adresse", "mail@test.com"));

        Optional<Annonce> found = repository.findById(annonce.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Titre");
    }

    @Test
    void update_shouldModifyAnnonce() {
        Annonce annonce = repository.save(new Annonce("Titre", "Description", "Adresse", "mail@test.com"));
        annonce.setTitle("Nouveau Titre");

        Annonce updated = repository.update(annonce);

        assertThat(updated.getTitle()).isEqualTo("Nouveau Titre");
    }

    @Test
    void deleteById_shouldRemoveAnnonce() {
        Annonce annonce = repository.save(new Annonce("Titre", "Description", "Adresse", "mail@test.com"));

        boolean deleted = repository.deleteById(annonce.getId());

        assertThat(deleted).isTrue();
        assertThat(repository.findById(annonce.getId())).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllAnnonces() {
        repository.save(new Annonce("Titre 1", "Desc 1", "Addr 1", "mail1@test.com"));
        repository.save(new Annonce("Titre 2", "Desc 2", "Addr 2", "mail2@test.com"));

        List<Annonce> all = repository.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void findAll_withPagination_shouldReturnPage() {
        for (int i = 0; i < 15; i++) {
            repository.save(new Annonce("Titre " + i, "Desc " + i, "Addr " + i, "mail" + i + "@test.com"));
        }

        List<Annonce> page0 = repository.findAll(0, 5);
        List<Annonce> page1 = repository.findAll(1, 5);

        assertThat(page0).hasSize(5);
        assertThat(page1).hasSize(5);
    }

    @Test
    void searchByKeyword_shouldFindByTitle() {
        repository.save(new Annonce("Voiture à vendre", "Belle voiture", "Paris", "mail@test.com"));
        repository.save(new Annonce("Appartement", "Bel appartement", "Lyon", "mail2@test.com"));

        List<Annonce> results = repository.searchByKeyword("voiture");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).contains("Voiture");
    }

    @Test
    void searchByKeyword_shouldFindByDescription() {
        repository.save(new Annonce("A vendre", "Belle voiture occasion", "Paris", "mail@test.com"));
        repository.save(new Annonce("Appartement", "Bel appartement", "Lyon", "mail2@test.com"));

        List<Annonce> results = repository.searchByKeyword("voiture");

        assertThat(results).hasSize(1);
    }

    @Test
    void findByStatus_shouldFilterByStatus() {
        Annonce draft = repository.save(new Annonce("Draft", "Desc", "Addr", "mail@test.com"));
        Annonce published = repository.save(new Annonce("Published", "Desc", "Addr", "mail2@test.com"));
        published.publish();
        repository.update(published);

        List<Annonce> drafts = repository.findByStatus(AnnonceStatus.DRAFT);
        List<Annonce> publishedList = repository.findByStatus(AnnonceStatus.PUBLISHED);

        assertThat(drafts).hasSize(1);
        assertThat(publishedList).hasSize(1);
    }

    @Test
    void findByCategory_shouldFilterByCategory() {
        Category cat1 = categoryRepository.save(new Category("Immobilier"));
        Category cat2 = categoryRepository.save(new Category("Auto"));

        Annonce a1 = new Annonce("Annonce 1", "Desc", "Addr", "mail@test.com");
        a1.setCategory(cat1);
        repository.save(a1);

        Annonce a2 = new Annonce("Annonce 2", "Desc", "Addr", "mail2@test.com");
        a2.setCategory(cat2);
        repository.save(a2);

        List<Annonce> immobilier = repository.findByCategory(cat1.getId());

        assertThat(immobilier).hasSize(1);
        assertThat(immobilier.get(0).getTitle()).isEqualTo("Annonce 1");
    }

    @Test
    void findByAuthor_shouldFilterByAuthor() {
        User author1 = userRepository.save(new User("user1", "user1@test.com", "password"));
        User author2 = userRepository.save(new User("user2", "user2@test.com", "password"));

        Annonce a1 = new Annonce("Annonce 1", "Desc", "Addr", "mail@test.com");
        a1.setAuthor(author1);
        repository.save(a1);

        Annonce a2 = new Annonce("Annonce 2", "Desc", "Addr", "mail2@test.com");
        a2.setAuthor(author2);
        repository.save(a2);

        List<Annonce> byAuthor1 = repository.findByAuthor(author1.getId());

        assertThat(byAuthor1).hasSize(1);
        assertThat(byAuthor1.get(0).getTitle()).isEqualTo("Annonce 1");
    }

    @Test
    void countByStatus_shouldReturnCorrectCount() {
        repository.save(new Annonce("Draft 1", "Desc", "Addr", "mail@test.com"));
        repository.save(new Annonce("Draft 2", "Desc", "Addr", "mail2@test.com"));
        Annonce published = repository.save(new Annonce("Published", "Desc", "Addr", "mail3@test.com"));
        published.publish();
        repository.update(published);

        long draftCount = repository.countByStatus(AnnonceStatus.DRAFT);
        long publishedCount = repository.countByStatus(AnnonceStatus.PUBLISHED);

        assertThat(draftCount).isEqualTo(2);
        assertThat(publishedCount).isEqualTo(1);
    }

    @Test
    void count_shouldReturnTotalCount() {
        repository.save(new Annonce("Titre 1", "Desc", "Addr", "mail@test.com"));
        repository.save(new Annonce("Titre 2", "Desc", "Addr", "mail2@test.com"));

        long count = repository.count();

        assertThat(count).isEqualTo(2);
    }
}
