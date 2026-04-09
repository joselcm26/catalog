package com.josec.catalog.controller;

import com.josec.catalog.dto.BookListRequestDTO;
import com.josec.catalog.dto.BookListResponseDTO;
import com.josec.catalog.service.BookListService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
public class BookListController {

    // --- DEPENDENCIAS ---

    @Autowired
    private BookListService bookListService;


    // --- CRUD BÁSICO ---

    @GetMapping("/{id}")
    public ResponseEntity<BookListResponseDTO> getBookList(@PathVariable int id) {
       return ResponseEntity.ok(bookListService.getBookList(id));
    }
    @GetMapping("/mylists")
    public ResponseEntity<List<BookListResponseDTO>> getMyBookLists() {
        return ResponseEntity.ok(bookListService.getUserLists());
    }
    @PostMapping
    public ResponseEntity<BookListResponseDTO> postBookList(@Valid @RequestBody BookListRequestDTO bookListRequestDTO) {
        BookListResponseDTO postedBookList = bookListService.createList(bookListRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(postedBookList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookListResponseDTO> deleteBookList(@PathVariable int id) {
        BookListResponseDTO deletedBookList = bookListService.deleteBookList(id);
        return ResponseEntity.noContent().build();
    }


    // -- GESTION DE LIBROS (Sub-recursos) ---

    @PostMapping("/{listId}/books/{bookId}")
    public ResponseEntity<BookListResponseDTO> addBookToList (
            @PathVariable int listId,
            @PathVariable int bookId ) {
        BookListResponseDTO savedBookList = bookListService.addBookToList(listId, bookId);
        return ResponseEntity.ok(savedBookList);
    }

    @DeleteMapping("/{listId}/books/{bookId}")
    public ResponseEntity<BookListResponseDTO> deleteBookFromList(
            @PathVariable int listId,
            @PathVariable int bookId) {
        BookListResponseDTO updatedBookList = bookListService.deleteBookFromList(listId, bookId);
        // Devolvemos 200 OK porque queremos que el Front-end vea cómo ha quedado la lista tras el borrado
        return ResponseEntity.ok(updatedBookList);
    }

    // --- GESTIÓN DE COLABORADORES (Sub-recursos) ---

    @PostMapping("/{listId}/collaborators/{userId}")
    public ResponseEntity<BookListResponseDTO> addCollaboratorToList (
            @PathVariable int listId,
            @PathVariable int userId){
        BookListResponseDTO updatedBookList = bookListService.addCollaboratorToList(listId, userId);
        return ResponseEntity.ok(updatedBookList);
    }

    @DeleteMapping("/{listId}/collaborators/{userId}")
    public ResponseEntity<BookListResponseDTO> deleteCollaboratorFromList(
            @PathVariable int listId,
            @PathVariable int userId){
        BookListResponseDTO updatedBookList = bookListService.deleteCollaboratorFromList(listId, userId);
        return ResponseEntity.ok(updatedBookList);
    }

}
