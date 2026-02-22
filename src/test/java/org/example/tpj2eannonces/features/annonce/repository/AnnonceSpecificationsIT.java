package org.example.tpj2eannonces.features.annonce.repository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.example.tpj2eannonces.TestcontainersConfig;
import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.example.tpj2eannonces.features.category.model.Category;
import org.example.tpj2eannonces.features.category.repository.CategoryRepository;
import org.example.tpj2eannonces.features.role.model.Role;
import org.example.tpj2eannonces.features.role.repository.RoleRepository;
import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfig.class)
@Transactional
class AnnonceSpecificationsIT {

    @Autowired
    private AnnonceRepository annonceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        annonceRepository.deleteAll();

        Role role = roleRepository.findAll().stream().findFirst()
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        testUser = new User("testuser", "testuser@test.com", "password");
        testUser.setRole(role);
        testUser = userRepository.save(testUser);

        testCategory = categoryRepository.findAll().stream().findFirst()
                .orElseGet(() -> categoryRepository.save(new Category("TestCategory")));

        Annonce annonce = new Annonce("Velo electrique", "Super velo a vendre", "Paris", "mail@test.com");
        annonce.setAuthor(testUser);
        annonce.setCategory(testCategory);
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        annonceRepository.save(annonce);

        Annonce annonce2 = new Annonce("Voiture occasion", "Belle voiture", "Lyon", "mail2@test.com");
        annonce2.setAuthor(testUser);
        annonce2.setCategory(testCategory);
        annonce2.setStatus(AnnonceStatus.DRAFT);
        annonceRepository.save(annonce2);
    }

    @Test
    void hasKeyword_shouldFilterByTitleOrDescription() {
        Specification<Annonce> spec = AnnonceSpecifications.hasKeyword("velo");
        Page<Annonce> result = annonceRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).contains("Velo");
    }

    @Test
    void hasStatus_shouldFilterByStatus() {
        Specification<Annonce> spec = AnnonceSpecifications.hasStatus(AnnonceStatus.DRAFT);
        Page<Annonce> result = annonceRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(AnnonceStatus.DRAFT);
    }

    @Test
    void hasCategoryId_shouldFilterByCategory() {
        Specification<Annonce> spec = AnnonceSpecifications.hasCategoryId(testCategory.getId());
        Page<Annonce> result = annonceRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void hasAuthorId_shouldFilterByAuthor() {
        Specification<Annonce> spec = AnnonceSpecifications.hasAuthorId(testUser.getId());
        Page<Annonce> result = annonceRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void createdAfter_shouldFilterByDate() {
        Specification<Annonce> spec = AnnonceSpecifications.createdAfter(LocalDateTime.now().minusDays(1));
        Page<Annonce> result = annonceRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void createdBefore_shouldFilterByDate() {
        Specification<Annonce> spec = AnnonceSpecifications.createdBefore(LocalDateTime.now().plusDays(1));
        Page<Annonce> result = annonceRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void combinedSpecs_shouldFilterCorrectly() {
        Specification<Annonce> spec = Specification.<Annonce>unrestricted()
                .and(AnnonceSpecifications.hasKeyword("velo"))
                .and(AnnonceSpecifications.hasStatus(AnnonceStatus.PUBLISHED));

        Page<Annonce> result = annonceRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void hasAuthorId_shouldReturnEmpty_whenNoMatch() {
        Specification<Annonce> spec = AnnonceSpecifications.hasAuthorId(UUID.randomUUID());
        Page<Annonce> result = annonceRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }
}
