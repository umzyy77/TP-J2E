package org.example.tpj2eannonces.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.tpj2eannonces.exception.category.CategoryInUseException;
import org.example.tpj2eannonces.exception.category.DuplicateCategoryException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

class CategoryServiceIT {

    private CategoryService service;

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
        service = new CategoryService();
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
    void create_shouldPersistCategory() {
        Category category = new Category("Immobilier");

        Category created = service.create(category);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getLabel()).isEqualTo("Immobilier");
    }

    @Test
    void create_shouldRejectDuplicates() {
        service.create(new Category("Immobilier"));
        Category duplicateCategory = new Category("Immobilier");

        assertThatThrownBy(() -> service.create(duplicateCategory))
                .isInstanceOf(DuplicateCategoryException.class)
                .hasMessageContaining("existe");
    }

    @Test
    void delete_shouldPreventIfAnnoncesExist() {
        Category category = service.create(new Category("Immobilier"));
        Long categoryId = category.getId();

        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            User author = new User("catowner", "catowner@test.com", "password");
            em.persist(author);

            Annonce annonce = new Annonce("Test", "Desc", "Addr", "mail@test.com");
            annonce.setAuthor(author);
            annonce.setCategory(em.find(Category.class, categoryId));
            em.persist(annonce);
            em.getTransaction().commit();
        }

        assertThatThrownBy(() -> service.delete(categoryId))
                .isInstanceOf(CategoryInUseException.class)
                .hasMessageContaining("annonce(s)");
    }

    @Test
    void findByLabel_shouldReturnCategory() {
        service.create(new Category("Auto"));

        var found = service.findByLabel("Auto");

        assertThat(found).isPresent();
        assertThat(found.get().getLabel()).isEqualTo("Auto");
    }

    @Test
    void findById_shouldReturnCategory() {
        Category created = service.create(new Category("Formation"));

        var found = service.findById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLabel()).isEqualTo("Formation");
    }

    @Test
    void findById_shouldReturnEmptyForNonexistent() {
        var found = service.findById(99999L);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllCategories() {
        service.create(new Category("Immobilier"));
        service.create(new Category("Auto"));
        service.create(new Category("Services"));

        var all = service.findAll();

        assertThat(all).hasSize(3);
    }

    @Test
    void delete_shouldRemoveCategoryWithoutAnnonces() {
        Category created = service.create(new Category("ToDelete"));

        boolean deleted = service.delete(created.getId());

        assertThat(deleted).isTrue();
        assertThat(service.findById(created.getId())).isEmpty();
    }

    @Test
    void update_shouldMergeCategory() {
        Category created = service.create(new Category("Initial"));
        created.setLabel("Updated");

        Category updated = service.update(created);

        assertThat(updated.getLabel()).isEqualTo("Updated");
        assertThat(service.findById(updated.getId())).isPresent();
        assertThat(service.findById(updated.getId()).orElseThrow().getLabel()).isEqualTo("Updated");
    }
}

