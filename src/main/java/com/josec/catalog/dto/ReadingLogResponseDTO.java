package com.josec.catalog.dto;

import com.josec.catalog.model.enums.Visibility;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReadingLogResponseDTO {

    private Integer id;
    private BookResponseDTO book;
    private LocalDate readDate;
    private Integer rating;
    private String privateComment;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private Visibility visibility;

    // Datos sociales para el feed
    private Integer likeCount;
    private Boolean iLikedIt;
    private List<CommentResponseDTO> comments;
}
