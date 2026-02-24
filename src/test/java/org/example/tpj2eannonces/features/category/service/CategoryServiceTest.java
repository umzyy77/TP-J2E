package org.example.tpj2eannonces.features.category.service;

import java.util.Optional;

import org.example.tpj2eannonces.features.category.model.Category;
import org.example.tpj2eannonces.features.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findById_shouldReturnCategory_whenExists() {
        Category category = new Category("Immobilier");
        category.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getLabel()).isEqualTo("Immobilier");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void getById_shouldReturnCategory_whenExists() {
        Category category = new Category("Immobilier");
        category.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getById(1L);

        assertThat(result.getLabel()).isEqualTo("Immobilier");
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(99L))
                .isInstanceOf(org.example.tpj2eannonces.features.category.exception.CategoryNotFoundException.class);
    }
}
