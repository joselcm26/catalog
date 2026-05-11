package com.josec.catalog.dto;

import lombok.Data;

@Data
public class ReviewResponseDTO {

    //Campos reseña
    private int id;
    private int rating;
    private String comment;

    //Campos autor
    private Long userId;
    private String userName;
}
