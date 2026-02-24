package org.example.tpj2eannonces.core.web.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiBusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    protected ApiBusinessException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
