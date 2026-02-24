package org.example.tpj2eannonces.features.category.exception;

import org.example.tpj2eannonces.core.web.exception.ApiBusinessException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends ApiBusinessException {

    public CategoryNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }
}
