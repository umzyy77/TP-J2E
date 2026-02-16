package org.example.tpj2eannonces.exception;

/**
 * Exception levee lorsqu'un utilisateur tente une action
 * sur une ressource qui ne lui appartient pas (403 Forbidden).
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
