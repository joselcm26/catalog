package com.josec.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CollaboratorAlreadyAddedException extends RuntimeException {
    public CollaboratorAlreadyAddedException(String message) {
        super(message);
    }
}
