package org.example.tpj2eannonces.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.example.tpj2eannonces.exception.ForbiddenException;
import org.example.tpj2eannonces.exception.NotFoundException;
import org.example.tpj2eannonces.exception.annonce.AnnonceImmutableException;
import org.example.tpj2eannonces.exception.annonce.ArchiveRequiredException;
import org.example.tpj2eannonces.exception.annonce.InvalidTransitionException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.repository.AnnonceRepository;
import org.example.tpj2eannonces.utils.PersistenceExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    @Mock
    private AnnonceRepository repository;

    @Mock
    private PersistenceExecutor persistenceExecutor;

    @Mock
    private EntityManager entityManager;

    private AnnonceService annonceService;

    @BeforeEach
    void setUp() {
        annonceService = new AnnonceService(repository, persistenceExecutor);
        stubPersistenceExecution();
    }

    @Test
    void constructor_withRepositoryOnly_shouldCreateService() {
        AnnonceService service = new AnnonceService(repository);

        assertThat(service).isNotNull();
    }

    @Test
    void create_shouldDelegateToRepository() {
        UUID authorId = UUID.randomUUID();
        Long categoryId = 10L;
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");
        when(repository.saveWithRelations(entityManager, annonce, authorId, categoryId)).thenReturn(annonce);

        Annonce created = annonceService.create(annonce, authorId, categoryId);

        assertThat(created).isSameAs(annonce);
        verify(repository).saveWithRelations(entityManager, annonce, authorId, categoryId);
    }

    @Test
    void update_shouldDelegateToRepository() {
        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");
        when(repository.update(entityManager, annonce)).thenReturn(annonce);

        Annonce updated = annonceService.update(annonce);

        assertThat(updated).isSameAs(annonce);
        verify(repository).update(entityManager, annonce);
    }

    @Test
    void changeStatus_shouldPublishWhenOwnerAndTransitionAreValid() {
        UUID ownerId = UUID.randomUUID();
        Annonce draft = annonceOwnedBy(ownerId, AnnonceStatus.DRAFT);
        when(repository.findById(entityManager, 1L)).thenReturn(Optional.of(draft));

        Annonce published = annonceOwnedBy(ownerId, AnnonceStatus.PUBLISHED);
        when(repository.updateStatus(entityManager, 1L, AnnonceStatus.PUBLISHED)).thenReturn(published);

        Annonce result = annonceService.changeStatus(1L, ownerId, "publish");

        assertThat(result.getStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
        verify(repository).updateStatus(entityManager, 1L, AnnonceStatus.PUBLISHED);
    }

    @Test
    void changeStatus_shouldRejectUnknownAction() {
        UUID ownerId = UUID.randomUUID();
        Annonce draft = annonceOwnedBy(ownerId, AnnonceStatus.DRAFT);
        when(repository.findById(entityManager, 1L)).thenReturn(Optional.of(draft));

        assertThatThrownBy(() -> annonceService.changeStatus(1L, ownerId, "unknown"))
                .isInstanceOf(InvalidTransitionException.class)
                .hasMessageContaining("Action inconnue");

        verify(repository, never()).updateStatus(any(), any(), any());
    }

    @Test
    void changeStatus_shouldRejectNonOwner() {
        UUID ownerId = UUID.randomUUID();
        UUID attackerId = UUID.randomUUID();
        Annonce draft = annonceOwnedBy(ownerId, AnnonceStatus.DRAFT);
        when(repository.findById(entityManager, 1L)).thenReturn(Optional.of(draft));

        assertThatThrownBy(() -> annonceService.changeStatus(1L, attackerId, "publish"))
                .isInstanceOf(ForbiddenException.class);

        verify(repository, never()).updateStatus(any(), any(), any());
    }

    @Test
    void updateFields_shouldRejectPublishedAnnonce() {
        UUID ownerId = UUID.randomUUID();
        Annonce published = annonceOwnedBy(ownerId, AnnonceStatus.PUBLISHED);
        when(repository.findById(entityManager, 1L)).thenReturn(Optional.of(published));

        assertThatThrownBy(() -> annonceService.updateFields(
                1L, ownerId, "New", "Desc", "Addr", "mail@test.com", null))
                .isInstanceOf(AnnonceImmutableException.class);
    }

    @Test
    void updateFields_shouldUpdateCategoryWhenProvided() {
        UUID ownerId = UUID.randomUUID();
        Annonce draft = annonceOwnedBy(ownerId, AnnonceStatus.DRAFT);
        when(repository.findById(entityManager, 1L)).thenReturn(Optional.of(draft));

        Category category = new Category("Auto");
        when(entityManager.find(Category.class, 5L)).thenReturn(category);

        Annonce result = annonceService.updateFields(
                1L, ownerId, "Nouveau", "Nouvelle description", "Nouvelle adresse", "new@test.com", 5L);

        assertThat(result.getTitle()).isEqualTo("Nouveau");
        assertThat(result.getCategory()).isSameAs(category);
    }

    @Test
    void updateFields_shouldRejectMissingCategory() {
        UUID ownerId = UUID.randomUUID();
        Annonce draft = annonceOwnedBy(ownerId, AnnonceStatus.DRAFT);
        when(repository.findById(entityManager, 1L)).thenReturn(Optional.of(draft));
        when(entityManager.find(Category.class, 999L)).thenReturn(null);

        assertThatThrownBy(() -> annonceService.updateFields(
                1L, ownerId, "New", "Desc", "Addr", "mail@test.com", 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Categorie non trouvee");
    }

    @Test
    void delete_shouldRejectAnnonceNotArchived() {
        UUID ownerId = UUID.randomUUID();
        Annonce published = annonceOwnedBy(ownerId, AnnonceStatus.PUBLISHED);
        when(repository.findById(entityManager, 1L)).thenReturn(Optional.of(published));

        assertThatThrownBy(() -> annonceService.delete(1L, ownerId))
                .isInstanceOf(ArchiveRequiredException.class);

        verify(repository, never()).deleteById(any(), any());
    }

    @Test
    void delete_shouldDeleteArchivedAnnonceForOwner() {
        UUID ownerId = UUID.randomUUID();
        Annonce archived = annonceOwnedBy(ownerId, AnnonceStatus.ARCHIVED);
        when(repository.findById(entityManager, 1L)).thenReturn(Optional.of(archived));
        when(repository.deleteById(entityManager, 1L)).thenReturn(true);

        boolean deleted = annonceService.delete(1L, ownerId);

        assertThat(deleted).isTrue();
        verify(repository).deleteById(entityManager, 1L);
    }

    @Test
    void countByKeyword_shouldDelegateToRepository() {
        when(repository.countByFilters(entityManager, "voiture", null, null)).thenReturn(3L);

        long count = annonceService.countByKeyword("voiture");

        assertThat(count).isEqualTo(3L);
        verify(repository).countByFilters(entityManager, "voiture", null, null);
    }

    private Annonce annonceOwnedBy(UUID ownerId, AnnonceStatus status) {
        User owner = new User("owner", "owner@test.com", "pwd");
        owner.setId(ownerId);

        Annonce annonce = new Annonce("Titre", "Desc", "Addr", "mail@test.com");
        annonce.setAuthor(owner);
        annonce.setStatus(status);
        return annonce;
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
