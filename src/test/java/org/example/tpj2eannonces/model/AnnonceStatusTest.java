package org.example.tpj2eannonces.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AnnonceStatusTest {

    @Test
    void getters_shouldReturnExpectedMetadata() {
        assertThat(AnnonceStatus.DRAFT.getActionName()).isEqualTo("publish");
        assertThat(AnnonceStatus.DRAFT.getActionLabel()).isEqualTo("Publier");
        assertThat(AnnonceStatus.DRAFT.getDisplayLabel()).isEqualTo("Brouillon");

        assertThat(AnnonceStatus.PUBLISHED.getActionName()).isEqualTo("archive");
        assertThat(AnnonceStatus.PUBLISHED.getActionLabel()).isEqualTo("Archiver");
        assertThat(AnnonceStatus.PUBLISHED.getDisplayLabel()).isEqualTo("Publié");

        assertThat(AnnonceStatus.ARCHIVED.getActionName()).isNull();
        assertThat(AnnonceStatus.ARCHIVED.getActionLabel()).isNull();
        assertThat(AnnonceStatus.ARCHIVED.getDisplayLabel()).isEqualTo("Archivé");
    }

    @Test
    void hasAction_shouldReturnTrueForDraftAndPublishedOnly() {
        assertThat(AnnonceStatus.DRAFT.hasAction()).isTrue();
        assertThat(AnnonceStatus.PUBLISHED.hasAction()).isTrue();
        assertThat(AnnonceStatus.ARCHIVED.hasAction()).isFalse();
    }

    @Test
    void getNextStatus_shouldFollowLifecycle() {
        assertThat(AnnonceStatus.DRAFT.getNextStatus()).isEqualTo(AnnonceStatus.PUBLISHED);
        assertThat(AnnonceStatus.PUBLISHED.getNextStatus()).isEqualTo(AnnonceStatus.ARCHIVED);
        assertThat(AnnonceStatus.ARCHIVED.getNextStatus()).isNull();
    }

    @Test
    void fromAction_shouldHandleKnownAndUnknownActions() {
        assertThat(AnnonceStatus.fromAction("publish")).contains(AnnonceStatus.DRAFT);
        assertThat(AnnonceStatus.fromAction("archive")).contains(AnnonceStatus.PUBLISHED);
        assertThat(AnnonceStatus.fromAction("unknown")).isEmpty();
    }

    @Test
    void getTargetStatusForAction_shouldReturnExpectedTarget() {
        assertThat(AnnonceStatus.getTargetStatusForAction("publish")).contains(AnnonceStatus.PUBLISHED);
        assertThat(AnnonceStatus.getTargetStatusForAction("archive")).contains(AnnonceStatus.ARCHIVED);
        assertThat(AnnonceStatus.getTargetStatusForAction("unknown")).isEmpty();
    }
}
