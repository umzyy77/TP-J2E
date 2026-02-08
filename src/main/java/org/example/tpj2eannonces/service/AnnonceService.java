package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.repository.AnnonceRepository;
import org.example.tpj2eannonces.utils.JPAUtil;

public class AnnonceService {

    private final AnnonceRepository repository;

    public AnnonceService() {
        this.repository = new AnnonceRepository();
    }

    public AnnonceService(AnnonceRepository repository) {
        this.repository = repository;
    }

    public Annonce create(Annonce annonce, UUID authorId, Long categoryId) {
        return JPAUtil.inTransaction(em -> repository.saveWithRelations(em, annonce, authorId, categoryId));
    }

    public Annonce update(Annonce annonce) {
        return JPAUtil.inTransaction(em -> repository.update(em, annonce));
    }

    public Annonce changeStatus(Long annonceId, String action) {
        return JPAUtil.inTransaction(em -> {
            Annonce annonce = repository.findById(em, annonceId)
                    .orElseThrow(() -> new ServiceException("Annonce non trouvee: " + annonceId));

            AnnonceStatus expectedCurrentStatus = AnnonceStatus.fromAction(action)
                    .orElseThrow(() -> new ServiceException("Action inconnue: " + action));

            if (annonce.getStatus() != expectedCurrentStatus) {
                throw new ServiceException("Transition invalide depuis " + annonce.getStatus() + " avec action " + action);
            }

            AnnonceStatus targetStatus = expectedCurrentStatus.getNextStatus();
            if (targetStatus == null) {
                throw new ServiceException("Aucun statut cible pour l'action: " + action);
            }

            return repository.updateStatus(em, annonceId, targetStatus);
        });
    }

    public boolean delete(Long annonceId) {
        return JPAUtil.inTransaction(em -> repository.deleteById(em, annonceId));
    }

    public Optional<Annonce> findById(Long id) {
        return JPAUtil.inReadOnly(em -> repository.findById(em, id));
    }

    public Optional<Annonce> findByIdWithRelations(Long id) {
        return JPAUtil.inReadOnly(em -> repository.findByIdWithRelations(em, id));
    }

    public List<Annonce> findAll(int page, int size) {
        return findByFilters(null, null, page, size);
    }

    public List<Annonce> findAllPublished(int page, int size) {
        return findByFilters(null, AnnonceStatus.PUBLISHED, page, size);
    }

    public List<Annonce> search(String keyword, int page, int size) {
        return searchByFilters(keyword, null, null, page, size);
    }

    public long countByKeyword(String keyword) {
        return countBySearchAndFilters(keyword, null, null);
    }

    public long count() {
        return JPAUtil.inReadOnly(repository::count);
    }

    public long countPublished() {
        return countByFilters(null, AnnonceStatus.PUBLISHED);
    }

    public List<Annonce> findByAuthor(UUID authorId, int page, int size) {
        return JPAUtil.inReadOnly(em -> repository.findByAuthor(em, authorId, page, size));
    }

    public long countByAuthor(UUID authorId) {
        return JPAUtil.inReadOnly(em -> repository.countByAuthor(em, authorId));
    }

    public List<Annonce> findByFilters(Long categoryId, AnnonceStatus status, int page, int size) {
        return searchByFilters(null, categoryId, status, page, size);
    }

    public long countByFilters(Long categoryId, AnnonceStatus status) {
        return countBySearchAndFilters(null, categoryId, status);
    }

    public List<Annonce> searchByFilters(String keyword, Long categoryId, AnnonceStatus status, int page, int size) {
        return JPAUtil.inReadOnly(em -> repository.findByFilters(em, keyword, categoryId, status, page, size));
    }

    public long countBySearchAndFilters(String keyword, Long categoryId, AnnonceStatus status) {
        return JPAUtil.inReadOnly(em -> repository.countByFilters(em, keyword, categoryId, status));
    }
}
