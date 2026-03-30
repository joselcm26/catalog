package com.josec.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // Le dice a Spring: "Escucha los errores de TODOS los controladores"
public class GlobalExceptionHandler {

    // 1. Manejador para cuando no encontramos un libro
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(BookNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage()); // Metemos tu mensaje personalizado

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 2. Manejador para los errores de @Valid (ej. título en blanco, rating incorrecto)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Recorremos todos los campos que hayan fallado la validación
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage); // Ej: "rating" : "The maximum rating is 5"
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
