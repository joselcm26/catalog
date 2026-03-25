package com.josec.catalog.controller;

import com.josec.catalog.model.Book;
import com.josec.catalog.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Controlador de libros. Aquí se utiliza el HTTP.

@RestController
@RequestMapping("/api/libros") //Todas las rutas empezarán por aquí
public class BookController {

    @Autowired
    private BookService bookService; //Inyección de repositorio para poder usarlo

    @GetMapping // GET HTTP
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(bookService.getAllBooks()); //Busca en la BD y lo devuelve
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable int id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping // POST HTTP
    public ResponseEntity<?> postBook(@RequestBody Book book) {
        Book savedBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook); //Guarda el book que nos llega en la BD
    }

    @PutMapping
    public ResponseEntity<?> putBook(@RequestBody Book book) {
        bookService.updateBook(book.getId().intValue(), book);
        return ResponseEntity.noContent().build();
    }
}
