package org.example.tpj2eannonces.features.annonce.model;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnonceStatusTest {

    @Test
    void draft_shouldHavePublishAction() {
        assertThat(AnnonceStatus.DRAFT.getActionName()).isEqualTo("publish");
        assertThat(AnnonceStatus.DRAFT.getActionLabel()).isEqualTo("Publier");
        assertThat(AnnonceStatus.DRAFT.getDisplayLabel()).isEqualTo("Brouillon");
        assertThat(AnnonceStatus.DRAFT.hasAction()).isTrue();
    }

    @Test
    void published_shouldHaveArchiveAction() {
        assertThat(AnnonceStatus.PUBLISHED.getActionName()).isEqualTo("archive");
        assertThat(AnnonceStatus.PUBLISHED.getActionLabel()).isEqualTo("Archiver");
        assertThat(AnnonceStatus.PUBLISHED.getDisplayLabel()).isEqualTo("Publié");
        assertThat(AnnonceStatus.PUBLISHED.hasAction()).isTrue();
    }

    @Test
    void archived_shouldHaveNoAction() {
        assertThat(AnnonceStatus.ARCHIVED.getActionName()).isNull();
        assertThat(AnnonceStatus.ARCHIVED.getActionLabel()).isNull();
        assertThat(AnnonceStatus.ARCHIVED.getDisplayLabel()).isEqualTo("Archivé");
        assertThat(AnnonceStatus.ARCHIVED.hasAction()).isFalse();
    }

    @Test
    void getNextStatus_draft_shouldReturnPublished() {
        assertThat(AnnonceStatus.DRAFT.getNextStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
    }

    @Test
    void getNextStatus_published_shouldReturnArchived() {
        assertThat(AnnonceStatus.PUBLISHED.getNextStatus()).isEqualTo(AnnonceStatus.ARCHIVED);
    }

    @Test
    void getNextStatus_archived_shouldReturnNull() {
        assertThat(AnnonceStatus.ARCHIVED.getNextStatus()).isNull();
    }

    @Test
    void fromAction_publish_shouldReturnDraft() {
        Optional<AnnonceStatus> result = AnnonceStatus.fromAction("publish");
        assertThat(result).contains(AnnonceStatus.DRAFT);
    }

    @Test
    void fromAction_archive_shouldReturnPublished() {
        Optional<AnnonceStatus> result = AnnonceStatus.fromAction("archive");
        assertThat(result).contains(AnnonceStatus.PUBLISHED);
    }

    @Test
    void fromAction_unknown_shouldReturnEmpty() {
        assertThat(AnnonceStatus.fromAction("unknown")).isEmpty();
    }

    @Test
    void fromAction_null_shouldReturnEmpty() {
        assertThat(AnnonceStatus.fromAction(null)).isEmpty();
    }

    @Test
    void getTargetStatusForAction_publish_shouldReturnPublished() {
        Optional<AnnonceStatus> result = AnnonceStatus.getTargetStatusForAction("publish");
        assertThat(result).contains(AnnonceStatus.PUBLISHED);
    }

    @Test
    void getTargetStatusForAction_archive_shouldReturnArchived() {
        Optional<AnnonceStatus> result = AnnonceStatus.getTargetStatusForAction("archive");
        assertThat(result).contains(AnnonceStatus.ARCHIVED);
    }

    @Test
    void getTargetStatusForAction_unknown_shouldReturnEmpty() {
        assertThat(AnnonceStatus.getTargetStatusForAction("unknown")).isEmpty();
    }
}
