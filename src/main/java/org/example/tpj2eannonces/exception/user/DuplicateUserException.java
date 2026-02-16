package org.example.tpj2eannonces.exception.user;

import org.example.tpj2eannonces.exception.ConflictException;

public class DuplicateUserException extends ConflictException {

    public DuplicateUserException(String field, String value) {
        super(field + " existe deja: " + value);
    }
}
