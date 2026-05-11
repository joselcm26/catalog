package com.josec.catalog.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true) //Campos del padre
public class ReadingLogRequestDTO extends MediaLogRequestDTO {


    @NotNull(message = "Book ID cannot be null")
    private Integer bookId;

    @NotNull(message = "Read date cannot be null")
    private LocalDate readDate;

    @Min(1) @Max(5)
    private Integer rating; // Puede ser nulo

    private String logComment; // Puede ser nulo
}
