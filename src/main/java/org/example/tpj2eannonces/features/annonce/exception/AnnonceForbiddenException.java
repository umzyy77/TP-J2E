package org.example.tpj2eannonces.features.annonce.exception;

import org.example.tpj2eannonces.core.web.exception.ApiBusinessException;
import org.springframework.http.HttpStatus;

public class AnnonceForbiddenException extends ApiBusinessException {

    public AnnonceForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }
}
