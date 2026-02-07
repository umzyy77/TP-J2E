package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.repository.AnnonceRepository;

public class AnnonceService {

    private final AnnonceRepository repository;

    public AnnonceService() {
        this.repository = new AnnonceRepository();
    }

    public AnnonceService(AnnonceRepository repository) {
        this.repository = repository;
    }

    public Annonce create(Annonce annonce, UUID authorId, UUID categoryId) {
        return repository.saveWithRelations(annonce, authorId, categoryId);
    }

    public Annonce update(Annonce annonce) {
        return repository.update(annonce);
    }

    public Annonce changeStatus(UUID annonceId, String action) {
        return AnnonceStatus.getTargetStatusForAction(action)
            .map(targetStatus -> repository.updateStatus(annonceId, targetStatus))
            .orElseThrow(() -> new ServiceException("Action inconnue: " + action));
    }

    public boolean delete(UUID annonceId) {
        return repository.deleteById(annonceId);
    }

    public Optional<Annonce> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Annonce> findByIdWithRelations(UUID id) {
        return repository.findByIdWithRelations(id);
    }

    public List<Annonce> findAll(int page, int size) {
        return repository.findAll(page, size);
    }

    public List<Annonce> findAllPublished(int page, int size) {
        return repository.findByStatus(AnnonceStatus.PUBLISHED, page, size);
    }

    public List<Annonce> search(String keyword, int page, int size) {
        return repository.searchByKeyword(keyword, page, size);
    }

    public long count() {
        return repository.count();
    }

    public List<Annonce> findByStatus(AnnonceStatus status, int page, int size) {
        return repository.findByStatus(status, page, size);
    }

    public long countByStatus(AnnonceStatus status) {
        return repository.countByStatus(status);
    }

    public long countPublished() {
        return repository.countByStatus(AnnonceStatus.PUBLISHED);
    }

    public List<Annonce> findByAuthor(UUID authorId, int page, int size) {
        return repository.findByAuthor(authorId, page, size);
    }

    public long countByAuthor(UUID authorId) {
        return repository.countByAuthor(authorId);
    }

    public List<Annonce> findByCategory(UUID categoryId, int page, int size) {
        return repository.findByCategory(categoryId, page, size);
    }

    public long countByCategory(UUID categoryId) {
        return repository.countByCategory(categoryId);
    }
}
