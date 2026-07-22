package com.devsenior.msal.reservation.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class BusinessRuleViolationException extends RuntimeException {

    private final HttpStatus httpStatus;

    public BusinessRuleViolationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
