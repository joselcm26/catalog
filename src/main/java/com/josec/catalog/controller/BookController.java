package com.josec.catalog.controller;

import com.josec.catalog.dto.BookRequestDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.model.Book;
import com.josec.catalog.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Controlador de libros. Aquí se utiliza el HTTP.

@RestController
@RequestMapping("/api/libros") //Todas las rutas empezarán por aquí
public class BookController {

    @Autowired
    private BookService bookService; //Inyección de repositorio para poder usarlo

    @GetMapping // GET HTTP
    public ResponseEntity<List<BookResponseDTO>> findAll() {
        return ResponseEntity.ok(bookService.getAllBooks()); //Busca en la BD y lo devuelve
    }

    // El @Valid le dice a Spring: "Antes de entrar aquí, comprueba que se cumplan
    // las reglas @NotBlank, @Min, etc. que pusimos en el DTO".
    @PostMapping // POST HTTP
    public ResponseEntity<BookResponseDTO> postBook(@Valid @RequestBody BookRequestDTO book) {
        BookResponseDTO savedBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook); //Guarda el book que nos llega en la BD
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> findById(@PathVariable int id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookResponseDTO> deleteById(@PathVariable int id) {
        bookService.deleteBook(id);
        // Cuando borras algo con éxito, lo estándar en APIs REST es devolver
        // un código 204 (No Content) en lugar de un 200.
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> putBook(@PathVariable int id, @Valid @RequestBody BookRequestDTO book ) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }
}
