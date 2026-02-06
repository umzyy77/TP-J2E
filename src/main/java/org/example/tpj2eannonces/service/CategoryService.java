package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.repository.CategoryRepository;

public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService() {
        this.repository = new CategoryRepository();
    }

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category create(Category category) {
        if (repository.existsByLabel(category.getLabel())) {
            throw new ServiceException("La catégorie existe déjà: " + category.getLabel());
        }
        return repository.save(category);
    }

    public Category update(Category category) {
        return repository.update(category);
    }

    public boolean delete(UUID categoryId) {
        long count = repository.countAnnoncesByCategory(categoryId);
        if (count > 0) {
            throw new ServiceException("Impossible de supprimer: " + count + " annonce(s) liée(s)");
        }
        return repository.deleteById(categoryId);
    }

    public Optional<Category> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Category> findByLabel(String label) {
        return repository.findByLabel(label);
    }

    public List<Category> findAll() {
        return repository.findAllOrderByLabel();
    }
}
