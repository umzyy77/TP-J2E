package org.example.tpj2eannonces.exception.category;

import org.example.tpj2eannonces.exception.ConflictException;

public class CategoryInUseException extends ConflictException {

    public CategoryInUseException(long count) {
        super("Impossible de supprimer: " + count + " annonce(s) liee(s)");
    }
}
