package com.josec.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EntryAlreadyExistException extends RuntimeException {
    public EntryAlreadyExistException(String message) {
        super(message);
    }
}
