package com.josec.catalog.service;


import com.josec.catalog.dto.BookRequestDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.ReviewRequestDTO;
import com.josec.catalog.dto.ReviewResponseDTO;
import com.josec.catalog.exception.BookNotFoundException;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.Review;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.BookRepository;
import com.josec.catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service // Le indicamos a Spring que esta es nuestra capa de lógica de negocio
public class BookService {

    // --- REPOSITORIOS ---

    @Autowired
    private BookRepository bookRepository; // Repo de libros

    @Autowired
    private UserRepository userRepository; // Repo de usuarios


    // --- MÉTODOS PRINCIPALES ---

    public Page<BookResponseDTO> getAllBooks(int page, int size) {
        // 1. Crear objeto de paginación con su tamaño
        Pageable pageable = PageRequest.of(page, size);

        // 2. El repositorio nos devuelve una página de libros, no una lista, con datos y metadatos
        Page<Book> bookPage = bookRepository.findAll(pageable);

        // 3. El objeto se puede mapear con .map() sin perder los metadatos
        return bookPage.map(this::mapToDTO);

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

    // PARA AÑADIR RESEÑAS
    public BookResponseDTO addReviewToBook(int bookId, ReviewRequestDTO reviewDTO) {
        // 1. Buscamos el libro al que le queremos poner la reseña
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        // 2. Buscamos al usuario
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + reviewDTO.getUserId()));

        // 3. Creamos la entidad Review a partir del DTO
        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        // 4. LA REGLA DE ORO BIDIRECCIONAL
        // Le decimos a la reseña cuál es su libro y usuario...
        review.setBook(book);
        review.setUser(user);
        // ... y le decimos al libro que tiene una nueva reseña
        book.getReviews().add(review);

        // 5. Guardamos el libro.
        // Con cascade = CascadeType.ALL en el modelo Book,
        // al guardar el libro, Spring Boot automáticamente guarda la reseña en la tabla 'reviews'.
        Book updatedBook = bookRepository.save(book);

        return mapToDTO(updatedBook);
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

        // Traducimos la lista de reseñas (Entidades) a una lista de ReviewResponseDTO
        if (book.getReviews() != null) {
            List<ReviewResponseDTO> reviewDTOs = book.getReviews().stream().map(review -> {
                ReviewResponseDTO reviewDTO = new ReviewResponseDTO();
                reviewDTO.setId(review.getId().intValue());
                reviewDTO.setRating(review.getRating());
                reviewDTO.setComment(review.getComment());
                reviewDTO.setUserId(review.getUser().getId().longValue());
                reviewDTO.setUsername(review.getUser().getUsername());
                return reviewDTO;
            }).collect(Collectors.toList());

            dto.setReviews(reviewDTOs);
        }

        return dto;
    }
}
