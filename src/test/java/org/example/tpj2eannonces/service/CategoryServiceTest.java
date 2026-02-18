package org.example.tpj2eannonces.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;

import org.example.tpj2eannonces.exception.category.CategoryInUseException;
import org.example.tpj2eannonces.exception.category.DuplicateCategoryException;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.repository.CategoryRepository;
import org.example.tpj2eannonces.utils.PersistenceExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @Mock
    private PersistenceExecutor persistenceExecutor;

    @Mock
    private EntityManager entityManager;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(repository, persistenceExecutor);
        stubPersistenceExecution();
    }

    @Test
    void constructor_withRepositoryOnly_shouldCreateService() {
        CategoryService service = new CategoryService(repository);

        assertThat(service).isNotNull();
    }

    @Test
    void create_shouldPersistCategoryWhenLabelIsUnique() {
        Category category = new Category("Immobilier");
        when(repository.existsByLabel(entityManager, "Immobilier")).thenReturn(false);
        when(repository.save(entityManager, category)).thenReturn(category);

        Category created = categoryService.create(category);

        assertThat(created).isSameAs(category);
        verify(repository).save(entityManager, category);
    }

    @Test
    void create_shouldRejectDuplicateLabel() {
        Category category = new Category("Immobilier");
        when(repository.existsByLabel(entityManager, "Immobilier")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(category))
                .isInstanceOf(DuplicateCategoryException.class)
                .hasMessageContaining("Immobilier");

        verify(repository, never()).save(any(), any());
    }

    @Test
    void delete_shouldRejectCategoryInUse() {
        when(repository.countAnnoncesByCategory(entityManager, 10L)).thenReturn(2L);

        assertThatThrownBy(() -> categoryService.delete(10L))
                .isInstanceOf(CategoryInUseException.class)
                .hasMessageContaining("2 annonce(s)");

        verify(repository, never()).deleteById(any(), any());
    }

    @Test
    void delete_shouldDelegateWhenCategoryIsUnused() {
        when(repository.countAnnoncesByCategory(entityManager, 10L)).thenReturn(0L);
        when(repository.deleteById(entityManager, 10L)).thenReturn(true);

        boolean deleted = categoryService.delete(10L);

        assertThat(deleted).isTrue();
        verify(repository).deleteById(entityManager, 10L);
    }

    @Test
    void findAll_shouldDelegateToRepository() {
        List<Category> categories = List.of(new Category("Auto"), new Category("Services"));
        when(repository.findAllOrderByLabel(entityManager)).thenReturn(categories);

        List<Category> result = categoryService.findAll();

        assertThat(result).containsExactlyElementsOf(categories);
        verify(repository).findAllOrderByLabel(entityManager);
    }

    private void stubPersistenceExecution() {
        lenient().when(persistenceExecutor.inTransaction(any())).thenAnswer(invocation -> {
            Function<EntityManager, Object> action = invocation.getArgument(0);
            return action.apply(entityManager);
        });
        lenient().when(persistenceExecutor.inReadOnly(any())).thenAnswer(invocation -> {
            Function<EntityManager, Object> action = invocation.getArgument(0);
            return action.apply(entityManager);
        });
    }
}
