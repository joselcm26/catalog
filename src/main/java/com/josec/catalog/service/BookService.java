package com.josec.catalog.service;


import com.josec.catalog.dto.BookRequestDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.ReviewRequestDTO;
import com.josec.catalog.dto.mappers.BookMapper;
import com.josec.catalog.exception.BookNotFoundException;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.Review;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.BookRepository;
import com.josec.catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

@Service // Le indicamos a Spring que esta es nuestra capa de lógica de negocio
public class BookService {

    // --- REPOSITORIOS ---

    @Autowired
    private BookRepository bookRepository; // Repo de libros

    @Autowired
    private UserRepository userRepository; // Repo de usuarios

    @Autowired
    private BookMapper bookMapper;




    // --- MÉTODOS PRINCIPALES ---

    public Page<BookResponseDTO> getAllBooks(int page, int size) {
        // 1. Crear objeto de paginación con su tamaño
        Pageable pageable = PageRequest.of(page, size);

        // 2. El repositorio nos devuelve una página de libros, no una lista, con datos y metadatos
        Page<Book> bookPage = bookRepository.findAll(pageable);

        // 3. El objeto se puede mapear con .map() sin perder los metadatos
        return bookPage.map(bookMapper::toDTO);

    }

    public BookResponseDTO createBook(BookRequestDTO requestDTO) {
       if (requestDTO != null) {
           Book book = bookMapper.toEntity(requestDTO); //Traducir el DTO a entidad
           Book savedBook = bookRepository.save(book);//Guardar en BD
           return bookMapper.toDTO(savedBook);//Traducir a DTO para devolver
       }
       throw new RuntimeException("Book could not be created");
    }


    public BookResponseDTO getBookById(int id) {
        Book book =  bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(("No se encontró el libro con el ID: " + id)));
        return bookMapper.toDTO(book);
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
        return bookMapper.toDTO(updatedBook);

    }

    public void deleteBook(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        bookRepository.delete(book);
    }

    // PARA AÑADIR RESEÑAS
    public BookResponseDTO addReviewToBook(int bookId, ReviewRequestDTO reviewDTO) {
        // 1. Buscamos el libro al que le queremos poner la reseña
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        // 2. Extraemos el nombre del usuario logueado directamente del contexto de seguridad de Spring
        String loggedInUsername = Objects
                .requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication())
                .getName();

        // 3. Buscamos al usuario en la base de datos usando ese nombre
        User user = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + loggedInUsername));

        // 4. Creamos la entidad Review a partir del DTO
        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        // 5. LA REGLA DE ORO BIDIRECCIONAL
        // Le decimos a la reseña cuál es su libro y usuario...
        review.setBook(book);
        review.setUser(user);// Asignamos el usuario que Spring nos ha garantizado que es el real
        // ... y le decimos al libro que tiene una nueva reseña
        book.getReviews().add(review);

        // 6. Guardamos el libro.
        // Con cascade = CascadeType.ALL en el modelo Book,
        // al guardar el libro, Spring Boot automáticamente guarda la reseña en la tabla 'reviews'.
        Book updatedBook = bookRepository.save(book);

        return bookMapper.toDTO(updatedBook);
    }

    // --- BÚSQUEDAS ---

    /**
     * Buscar libros por TÍTULO en la base de datos global
     *
     * @param query clave de búsqueda
     * @param page página a devolver
     * @param size tamaño de la página
     * @return página con libros si hay coincidencias
     */
    public Page<BookResponseDTO> searchBooksGlobal(String query, int page, int size) {

        // 1. Crear la paginación
        Pageable pageable = PageRequest.of(page, size);

        // 2. Hacer consulta paginada
        Page<Book> bookPage = bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query, pageable);

        // 3. Traducir a DTO
        return bookPage.map(bookMapper::toDTO);
    }


    /**
     * Actualizar el cover del libro
     * @param id del libro
     * @param filename nombre del archivo
     * @return libro actualizado en DTO
     */
    public BookResponseDTO updateCoverImage(int id, String filename){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        book.setCoverImage(filename);
        bookRepository.save(book);
        return bookMapper.toDTO(book);
    }
}
