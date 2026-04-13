package com.josec.catalog.controller;

import com.josec.catalog.dto.BookRequestDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.ReviewRequestDTO;
import com.josec.catalog.service.BookService;
import com.josec.catalog.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//Controlador de libros. Aquí se utiliza el HTTP.

@RestController
@RequestMapping("/api/books") //Todas las rutas empezarán por aquí
public class BookController {

    @Autowired
    private BookService bookService; //Inyección de repositorio para poder usarlo

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping // GET HTTP
    public ResponseEntity<Page<BookResponseDTO>> findAll(
            //Parámetros de página
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
       ) {
       return ResponseEntity.ok(bookService.getAllBooks(page, size));
    }

    // El @Valid le dice a Spring: "Antes de entrar aquí, comprueba que se cumplan
    // las reglas @NotBlank, @Min, etc. que pusimos en el DTO".
    @PostMapping // POST HTTP
    public ResponseEntity<BookResponseDTO> postBook(@Valid @RequestBody BookRequestDTO book) {
        BookResponseDTO savedBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook); //Guarda el book que nos llega en la BD
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<BookResponseDTO> addReviewToBook(
            @PathVariable("id") Integer id,
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        BookResponseDTO updatedBook = bookService.addReviewToBook(id, reviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedBook);
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

    // -- BÚSQUEDA --
    // GET /api/books/search?query=busqueda&page=0&size=10
    @GetMapping("/search")
    public ResponseEntity<Page<BookResponseDTO>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,   //Página y tamaño por defecto
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.searchBooksGlobal(query, page, size));
    }

    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponseDTO> uploadCoverImage(
            @PathVariable int id,
            @RequestParam("file") MultipartFile file
    ) {
        // 1. Guardar archivo en el disco duro y obtenemos su nombre
        String filename = fileStorageService.saveCoverImage(file);

        // 2. Actualizar libro en la base de datos
        BookResponseDTO updatedBook = bookService.updateCoverImage(id, filename);
        return ResponseEntity.ok(updatedBook);
    }
}
