package org.example.tpj2eannonces.exception.category;

import org.example.tpj2eannonces.exception.ConflictException;

public class DuplicateCategoryException extends ConflictException {

    public DuplicateCategoryException(String label) {
        super("La categorie existe deja: " + label);
    }
}
