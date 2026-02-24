package org.example.tpj2eannonces.features.user.exception;

import org.example.tpj2eannonces.core.web.exception.ApiBusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiBusinessException {

    public UserNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }
}
