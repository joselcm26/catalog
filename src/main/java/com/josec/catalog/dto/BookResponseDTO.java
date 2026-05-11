package com.josec.catalog.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookResponseDTO {
    private int id;
    private String title;
    private String author;
    private Integer publicationYear;
    private String synopsis;
    private String coverImage; // URL completa de la imagen

    // Reviews
    private List<ReviewResponseDTO> reviews;

    //Nota media de reviews
    private Double averageRating;
}
