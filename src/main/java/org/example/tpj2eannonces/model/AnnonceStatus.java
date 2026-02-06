package org.example.tpj2eannonces.model;

import java.util.Optional;

public enum AnnonceStatus {
    DRAFT("publish", "Publier"),
    PUBLISHED("archive", "Archiver"),
    ARCHIVED(null, null);

    private final String actionName;
    private final String actionLabel;

    AnnonceStatus(String actionName, String actionLabel) {
        this.actionName = actionName;
        this.actionLabel = actionLabel;
    }

    public AnnonceStatus getNextStatus() {
        int nextIndex = this.ordinal() + 1;
        AnnonceStatus[] statuses = values();
        return nextIndex < statuses.length ? statuses[nextIndex] : null;
    }

    public String getActionName() {
        return actionName;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public boolean hasAction() {
        return actionName != null;
    }

    public static Optional<AnnonceStatus> fromAction(String action) {
        for (AnnonceStatus status : values()) {
            if (status.actionName != null && status.actionName.equals(action)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }

    public static Optional<AnnonceStatus> getTargetStatusForAction(String action) {
        return fromAction(action).map(AnnonceStatus::getNextStatus);
    }
}
