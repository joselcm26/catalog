package com.josec.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReadListNotFoundException extends RuntimeException {
    public ReadListNotFoundException(String message) {
        super(message);
    }
}
