package com.josec.catalog.service;


import com.josec.catalog.dto.BookRequestDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.ReviewRequestDTO;
import com.josec.catalog.dto.ReviewResponseDTO;
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

import java.util.List;
import java.util.Objects;
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

        return mapToDTO(updatedBook);
    }

    // --- MÉTODOS TRADUCTORES (MAPPERS) ---

    /**
     * Traducción de DTO a entidad.
     *
     * @param dto a convertir
     * @return Entidad @Book traducida
     */
    private Book mapToEntity(BookRequestDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublicationYear(dto.getPublicationYear());
        book.setSynopsis(dto.getSynopsis());

        //TODO: mapear reviews

        return book;
    }

    /**
     * Traduce un objeto @Book a un objeto BookResponseDTO. Además, calcula
     * la nota media de las reviews.
     *
     * @param book a convertir a DTO
     * @return DTO con nota media
     */
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

            // Calcular la media de las reviews del libro

            //TODO: refactorizar a una clase a parte
            //Cogemos las reseñas -> Extraemos solo la nota (mapToDouble)
            // -> Calculamos la media (average) -> Si falla, devolvemos 0.0
            double average = book.getReviews().stream()
                            .mapToDouble(Review::getRating)
                            .average()
                            .orElse(0.0);

            // Redondear a 1 decimal solamente y guardar en DTO
            double roundedAverage = Math.round(average * 10.0) / 10.0;
            dto.setAverageRating(roundedAverage);

        }else{
            // Libro sin reseñas
            dto.setAverageRating(0.0);
        }

        return dto;
    }
}
