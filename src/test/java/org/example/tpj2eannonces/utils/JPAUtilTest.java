package org.example.tpj2eannonces.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

class JPAUtilTest {

    @BeforeAll
    static void setUp() {
        JPAUtil.getEntityManagerFactory();
    }

    @AfterAll
    static void tearDown() {
        JPAUtil.close();
    }

    @Test
    void getEntityManagerFactory_shouldReturnNonNullFactory() {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

        assertThat(emf).isNotNull();
        assertThat(emf.isOpen()).isTrue();
    }

    @Test
    void getEntityManager_shouldReturnOpenEntityManager() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            assertThat(em).isNotNull();
            assertThat(em.isOpen()).isTrue();
        }
    }

    @Test
    void entityManager_shouldConnectToDatabase() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Object result = em.createNativeQuery("SELECT 1").getSingleResult();
            assertThat(result).isNotNull();
        }
    }

    @Test
    void multipleEntityManagers_shouldBeIndependent() {
        EntityManager em1 = JPAUtil.getEntityManager();
        EntityManager em2 = JPAUtil.getEntityManager();

        assertThat(em1).isNotSameAs(em2);
        assertThat(em1.isOpen()).isTrue();
        assertThat(em2.isOpen()).isTrue();

        em1.close();
        assertThat(em1.isOpen()).isFalse();
        assertThat(em2.isOpen()).isTrue();

        em2.close();
    }
}
