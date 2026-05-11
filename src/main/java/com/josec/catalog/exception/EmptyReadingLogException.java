package com.josec.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmptyReadingLogException extends RuntimeException {
    public EmptyReadingLogException(String message) {
        super(message);
    }
}
