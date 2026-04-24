package com.josec.catalog.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReadingLogRequestDTO {


    @NotNull(message = "Book ID cannot be null")
    private Integer bookId;

    @NotNull(message = "Read date cannot be null")
    private LocalDate readDate;

    @Min(1) @Max(5)
    private Integer rating; // Puede ser nulo

    private String privateComment; // Puede ser nulo
}
