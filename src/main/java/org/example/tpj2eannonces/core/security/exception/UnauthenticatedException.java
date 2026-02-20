package org.example.tpj2eannonces.core.security.exception;

import org.example.tpj2eannonces.core.web.exception.ApiBusinessException;
import org.springframework.http.HttpStatus;

public class UnauthenticatedException extends ApiBusinessException {

    public UnauthenticatedException(String message) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
}
