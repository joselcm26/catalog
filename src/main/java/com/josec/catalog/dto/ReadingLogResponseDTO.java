package com.josec.catalog.dto;

import com.josec.catalog.model.enums.Visibility;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReadingLogResponseDTO {

    // Cosas de MediaLog (padre)
    private Integer id;
    private String username;
    private Visibility visibility;

    //Cosas de ReadingLog
    private BookResponseDTO book;
    private LocalDate readDate;
    private Integer rating;
    private String privateComment;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    // Datos sociales para el feed
    private Integer likeCount;
    private Boolean iLikedIt;

}
