package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.BookRequestDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.ReviewResponseDTO;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    /**
     * Traduce un objeto @Book a un objeto BookResponseDTO. Además, calcula
     * la nota media de las reviews.
     *
     * @param book a convertir a DTO
     * @return DTO con nota media
     */
    public BookResponseDTO mapToDTO(Book book) {
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

    /**
     * Traducción de DTO a entidad.
     *
     * @param dto a convertir
     * @return Entidad @Book traducida
     */
    public Book mapToEntity(BookRequestDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublicationYear(dto.getPublicationYear());
        book.setSynopsis(dto.getSynopsis());

        //TODO: mapear reviews

        return book;
    }
}