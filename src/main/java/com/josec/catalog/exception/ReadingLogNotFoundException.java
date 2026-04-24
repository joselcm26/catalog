package com.josec.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReadingLogNotFoundException extends RuntimeException {
    public ReadingLogNotFoundException(String message) {
        super(message);
    }
}
