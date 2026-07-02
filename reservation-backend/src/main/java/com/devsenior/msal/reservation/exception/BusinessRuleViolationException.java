package com.devsenior.msal.reservation.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleViolationException extends RuntimeException {

    private final HttpStatus httpStatus;

    public BusinessRuleViolationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BusinessRuleViolationException(String message) {
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
