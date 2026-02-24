package org.example.tpj2eannonces.features.annonce.exception;

import org.example.tpj2eannonces.core.web.exception.ApiBusinessException;
import org.springframework.http.HttpStatus;

public class AnnonceNotFoundException extends ApiBusinessException {

    public AnnonceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }
}
