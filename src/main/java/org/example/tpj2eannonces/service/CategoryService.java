package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.example.tpj2eannonces.exception.category.CategoryInUseException;
import org.example.tpj2eannonces.exception.category.DuplicateCategoryException;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.repository.CategoryRepository;
import org.example.tpj2eannonces.utils.JpaPersistenceExecutor;
import org.example.tpj2eannonces.utils.PersistenceExecutor;

public class CategoryService {

    private final CategoryRepository repository;
    private final PersistenceExecutor persistenceExecutor;

    public CategoryService() {
        this(new CategoryRepository(), new JpaPersistenceExecutor());
    }

    public CategoryService(CategoryRepository repository) {
        this(repository, new JpaPersistenceExecutor());
    }

    public CategoryService(CategoryRepository repository, PersistenceExecutor persistenceExecutor) {
        this.repository = Objects.requireNonNull(repository);
        this.persistenceExecutor = Objects.requireNonNull(persistenceExecutor);
    }

    public Category create(Category category) {
        return persistenceExecutor.inTransaction(em -> {
            if (repository.existsByLabel(em, category.getLabel())) {
                throw new DuplicateCategoryException(category.getLabel());
            }
            return repository.save(em, category);
        });
    }

    public Category update(Category category) {
        return persistenceExecutor.inTransaction(em -> repository.update(em, category));
    }

    public boolean delete(Long categoryId) {
        return persistenceExecutor.inTransaction(em -> {
            long count = repository.countAnnoncesByCategory(em, categoryId);
            if (count > 0) {
                throw new CategoryInUseException(count);
            }
            return repository.deleteById(em, categoryId);
        });
    }


    public Optional<Category> findById(Long id) {
        return persistenceExecutor.inReadOnly(em -> repository.findById(em, id));
    }

    public Optional<Category> findByLabel(String label) {
        return persistenceExecutor.inReadOnly(em -> repository.findByLabel(em, label));
    }

    public List<Category> findAll() {
        return persistenceExecutor.inReadOnly(repository::findAllOrderByLabel);
    }
}
