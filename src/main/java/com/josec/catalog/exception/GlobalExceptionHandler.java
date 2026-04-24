package com.josec.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // Le dice a Spring: "Escucha los errores de TODOS los controladores"
public class GlobalExceptionHandler {

    // 1. Manejador para cuando no encontramos un libro
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrors(ex));
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

    // 3. Manejador para cuando un nombre de usuario ya existe
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrors(ex));
    }

    // 5. Manejador para cuando un email de usuario ya existe
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrors(ex));
    }

    // 5. Manejador para usuario no existe
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrors(ex));
    }

    // 6. Colaborador existente
    @ExceptionHandler(CollaboratorAlreadyAddedException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(CollaboratorAlreadyAddedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrors(ex));
    }

    // 7. Acceso denegado
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrors(ex));
    }

    // 8. Reading log vacío
    @ExceptionHandler(EmptyReadingLogException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(EmptyReadingLogException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrors(ex));
    }

    // 9. Reading log no encontrado
    @ExceptionHandler(ReadingLogNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(ReadingLogNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrors(ex));
    }

    // Atrapa específicamente el error de límite de tamaño
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        // Devolvemos un 413 (Payload Too Large) en lugar de un 500
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
                .body("The file size is too large. Maximum size allowed is 5MB.");
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrors(ex));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrors(ex));
    }

    // -- Privados

    //Para evitar repetir código
    private Map<String, String> getErrors(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage()); // Metemos mensaje personalizado

        return response;
    }
}
