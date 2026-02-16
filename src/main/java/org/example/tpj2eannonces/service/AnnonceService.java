package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.exception.ForbiddenException;
import org.example.tpj2eannonces.exception.NotFoundException;
import org.example.tpj2eannonces.exception.annonce.AnnonceImmutableException;
import org.example.tpj2eannonces.exception.annonce.ArchiveRequiredException;
import org.example.tpj2eannonces.exception.annonce.InvalidTransitionException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.repository.AnnonceRepository;
import org.example.tpj2eannonces.utils.JPAUtil;

public class AnnonceService {

    private static final String ANNONCE_NOT_FOUND = "Annonce non trouvee: ";

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

    public Annonce updateFields(Long annonceId, UUID currentUserId, String title, String description,
                                String adress, String mail, Long categoryId) {
        return JPAUtil.inTransaction(em -> {
            Annonce existing = repository.findById(em, annonceId)
                    .orElseThrow(() -> new NotFoundException(ANNONCE_NOT_FOUND + annonceId));

            checkOwnership(existing, currentUserId);

            if (existing.getStatus() == AnnonceStatus.PUBLISHED) {
                throw new AnnonceImmutableException();
            }

            existing.setTitle(title);
            existing.setDescription(description);
            existing.setAdress(adress);
            existing.setMail(mail);

            if (categoryId != null) {
                Category category = em.find(Category.class, categoryId);
                if (category == null) {
                    throw new NotFoundException("Categorie non trouvee: " + categoryId);
                }
                existing.setCategory(category);
            }

            return existing;
        });
    }

    public Annonce changeStatus(Long annonceId, UUID currentUserId, String action) {
        return JPAUtil.inTransaction(em -> {
            Annonce annonce = repository.findById(em, annonceId)
                    .orElseThrow(() -> new NotFoundException(ANNONCE_NOT_FOUND + annonceId));

            checkOwnership(annonce, currentUserId);

            AnnonceStatus expectedCurrentStatus = AnnonceStatus.fromAction(action)
                    .orElseThrow(() -> new InvalidTransitionException(action));

            if (annonce.getStatus() != expectedCurrentStatus) {
                throw new InvalidTransitionException(annonce.getStatus(), action);
            }

            AnnonceStatus targetStatus = expectedCurrentStatus.getNextStatus();
            if (targetStatus == null) {
                throw new InvalidTransitionException(action);
            }

            return repository.updateStatus(em, annonceId, targetStatus);
        });
    }

    public boolean delete(Long annonceId, UUID currentUserId) {
        return JPAUtil.inTransaction(em -> {
            Annonce existing = repository.findById(em, annonceId)
                    .orElseThrow(() -> new NotFoundException(ANNONCE_NOT_FOUND + annonceId));

            checkOwnership(existing, currentUserId);

            if (existing.getStatus() != AnnonceStatus.ARCHIVED) {
                throw new ArchiveRequiredException();
            }

            return repository.deleteById(em, annonceId);
        });
    }

    private void checkOwnership(Annonce annonce, UUID currentUserId) {
        if (currentUserId == null || !currentUserId.equals(annonce.getOwnerId())) {
            throw new ForbiddenException("Vous n'etes pas l'auteur de cette annonce");
        }
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
