package org.example.tpj2eannonces.features.auth.exception;

import org.example.tpj2eannonces.core.web.exception.ApiBusinessException;
import org.springframework.http.HttpStatus;

public class AuthUnauthorizedException extends ApiBusinessException {

    public AuthUnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
}
