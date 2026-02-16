package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;

import org.example.tpj2eannonces.exception.category.CategoryInUseException;
import org.example.tpj2eannonces.exception.category.DuplicateCategoryException;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.repository.CategoryRepository;
import org.example.tpj2eannonces.utils.JPAUtil;

public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService() {
        this.repository = new CategoryRepository();
    }

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category create(Category category) {
        return JPAUtil.inTransaction(em -> {
            if (repository.existsByLabel(em, category.getLabel())) {
                throw new DuplicateCategoryException(category.getLabel());
            }
            return repository.save(em, category);
        });
    }

    public Category update(Category category) {
        return JPAUtil.inTransaction(em -> repository.update(em, category));
    }

    public boolean delete(Long categoryId) {
        return JPAUtil.inTransaction(em -> {
            long count = repository.countAnnoncesByCategory(em, categoryId);
            if (count > 0) {
                throw new CategoryInUseException(count);
            }
            return repository.deleteById(em, categoryId);
        });
    }


    public Optional<Category> findById(Long id) {
        return JPAUtil.inReadOnly(em -> repository.findById(em, id));
    }

    public Optional<Category> findByLabel(String label) {
        return JPAUtil.inReadOnly(em -> repository.findByLabel(em, label));
    }

    public List<Category> findAll() {
        return JPAUtil.inReadOnly(repository::findAllOrderByLabel);
    }
}
