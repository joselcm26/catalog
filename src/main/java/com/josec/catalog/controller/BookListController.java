package com.josec.catalog.controller;

import com.josec.catalog.dto.BookListResponseDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.service.BookListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
public class BookListController {

    // --- DEPENDENCIAS ---

    @Autowired
    private BookListService bookListService;

    @GetMapping("{id}")
    public ResponseEntity<BookListResponseDTO> getAllBooks(@PathVariable int id) {
       return ResponseEntity.ok(bookListService.getBookList(id));
    }
}
