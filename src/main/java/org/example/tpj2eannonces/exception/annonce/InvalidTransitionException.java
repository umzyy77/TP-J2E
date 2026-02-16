package org.example.tpj2eannonces.exception.annonce;

import org.example.tpj2eannonces.exception.ConflictException;
import org.example.tpj2eannonces.model.AnnonceStatus;

public class InvalidTransitionException extends ConflictException {

    public InvalidTransitionException(AnnonceStatus currentStatus, String action) {
        super("Transition invalide depuis " + currentStatus + " avec action " + action);
    }

    public InvalidTransitionException(String action) {
        super("Action inconnue: " + action);
    }
}
