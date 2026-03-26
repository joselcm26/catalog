package com.josec.catalog.dto;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private int id;
    private int rating;
    private String comment;
}
