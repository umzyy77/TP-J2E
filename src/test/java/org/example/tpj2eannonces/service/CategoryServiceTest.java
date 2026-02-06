package org.example.tpj2eannonces.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.utils.JPAUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

class CategoryServiceTest {

    private CategoryService service;
    private AnnonceService annonceService;

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
        annonceService = new AnnonceService();
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
            em.createQuery("DELETE FROM Category").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
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

        assertThatThrownBy(() -> service.create(new Category("Immobilier")))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("existe déjà");
    }

    @Test
    void delete_shouldPreventIfAnnoncesExist() {
        Category category = service.create(new Category("Immobilier"));

        // Créer une annonce liée à cette catégorie
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            var annonce = new org.example.tpj2eannonces.model.Annonce("Test", "Desc", "Addr", "mail@test.com");
            annonce.setCategory(category);
            em.persist(annonce);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        assertThatThrownBy(() -> service.delete(category.getId()))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("annonce(s) liée(s)");
    }

    @Test
    void findByLabel_shouldReturnCategory() {
        service.create(new Category("Auto"));

        var found = service.findByLabel("Auto");

        assertThat(found).isPresent();
        assertThat(found.get().getLabel()).isEqualTo("Auto");
    }
}
