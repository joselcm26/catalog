package com.josec.catalog.service;


import com.josec.catalog.dto.BookRequestDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.exception.BookNotFoundException;
import com.josec.catalog.model.Book;
import com.josec.catalog.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // Le indicamos a Spring que esta es nuestra capa de lógica de negocio
public class BookService {

    @Autowired
    private BookRepository bookRepository; // El servicio habla con la base de datos

    // --- MÉTODOS PRINCIPALES ---

    public List<BookResponseDTO> getAllBooks() {

        List<Book> books = bookRepository.findAll();

        //Convertir la lista de entidades book a una lista BookResponseDTO
        return books.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BookResponseDTO createBook(BookRequestDTO requestDTO) {
       if (requestDTO != null) {
           Book book = mapToEntity(requestDTO); //Traducir el DTO a entidad
           Book savedBook = bookRepository.save(book);//Guardar en BD
           return mapToDTO(savedBook);//Traducir a DTO para devolver
       }
       throw new RuntimeException("Book could not be created");
    }


    public BookResponseDTO getBookById(int id) {
        Book book =  bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(("No se encontró el libro con el ID: " + id)));
        return mapToDTO(book);
    }

    public BookResponseDTO updateBook(int id, BookRequestDTO requestDTO) {

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        // Actualizamos los datos de la entidad con los datos del DTO
        existingBook.setTitle(requestDTO.getTitle());
        existingBook.setAuthor(requestDTO.getAuthor());
        existingBook.setPublicationYear(requestDTO.getPublicationYear());
        existingBook.setSynopsis(requestDTO.getSynopsis());

        Book updatedBook = bookRepository.save(existingBook);
        return mapToDTO(updatedBook);

    }

    public void deleteBook(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        bookRepository.delete(book);
    }

    // --- MÉTODOS TRADUCTORES (MAPPERS) ---

    private Book mapToEntity(BookRequestDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublicationYear(dto.getPublicationYear());
        book.setSynopsis(dto.getSynopsis());

        //TODO: mapear reviews

        return book;
    }

    private BookResponseDTO mapToDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId().intValue()); // Asumo que en tu modelo Book tienes un getId() o la anotación @Data
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setSynopsis(book.getSynopsis());

        //TODO: mapear reviews

        return dto;
    }
}
