package com.josec.catalog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=true) // Para que lombok tenga en cuenta los campos del padre
public class ReadingLogResponseDTO extends MediaLogResponseDTO {

    private BookResponseDTO book;
    private LocalDate readDate;
    private Integer rating;
    private String privateComment;
    private LocalDateTime deletedAt;



}
