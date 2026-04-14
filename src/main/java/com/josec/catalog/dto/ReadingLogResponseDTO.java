package com.josec.catalog.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReadingLogResponseDTO {

    private Integer id;
    private BookResponseDTO book;
    private LocalDate readDate;
    private Integer rating;
    private String privateComment;
    private LocalDateTime createdAt;
}
