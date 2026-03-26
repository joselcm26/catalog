package com.josec.catalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookRequestDTO {

    // @NotBlank asegura que no nos envíen un título vacío o solo con espacios
    @NotBlank(message = "The title is mandatory")
    private String title;

    @NotBlank(message = "The author is mandatory")
    private String author;

    // Validamos que sea un año con sentido
    @Min(value = 1000, message = "The year must be valid")
    @Max(value = 2100, message = "The year cannot be in the future")
    private Integer publicationYear;

    private String synopsis; // La sinopsis la dejamos opcional
}
