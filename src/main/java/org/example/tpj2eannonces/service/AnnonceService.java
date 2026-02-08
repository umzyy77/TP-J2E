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
        return JPAUtil.inReadOnly(em -> repository.findAllWithRelations(em, page, size));
    }

    public List<Annonce> findAllPublished(int page, int size) {
        return JPAUtil.inReadOnly(em -> repository.findByStatus(em, AnnonceStatus.PUBLISHED, page, size));
    }

    public List<Annonce> search(String keyword, int page, int size) {
        return JPAUtil.inReadOnly(em -> repository.searchByKeyword(em, keyword, page, size));
    }

    public long countByKeyword(String keyword) {
        return JPAUtil.inReadOnly(em -> repository.countByKeyword(em, keyword));
    }

    public long count() {
        return JPAUtil.inReadOnly(repository::count);
    }

    public List<Annonce> findByStatus(AnnonceStatus status, int page, int size) {
        return JPAUtil.inReadOnly(em -> repository.findByStatus(em, status, page, size));
    }

    public long countByStatus(AnnonceStatus status) {
        return JPAUtil.inReadOnly(em -> repository.countByStatus(em, status));
    }

    public long countPublished() {
        return JPAUtil.inReadOnly(em -> repository.countByStatus(em, AnnonceStatus.PUBLISHED));
    }

    public List<Annonce> findByAuthor(UUID authorId, int page, int size) {
        return JPAUtil.inReadOnly(em -> repository.findByAuthor(em, authorId, page, size));
    }

    public long countByAuthor(UUID authorId) {
        return JPAUtil.inReadOnly(em -> repository.countByAuthor(em, authorId));
    }

    public List<Annonce> findByCategory(Long categoryId, int page, int size) {
        return JPAUtil.inReadOnly(em -> repository.findByCategory(em, categoryId, page, size));
    }

    public long countByCategory(Long categoryId) {
        return JPAUtil.inReadOnly(em -> repository.countByCategory(em, categoryId));
    }
}
