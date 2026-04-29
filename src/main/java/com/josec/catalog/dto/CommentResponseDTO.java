package com.josec.catalog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {

    private String content;
    private Long userId;
    private String username;
    private Integer mediaLogId;
    private LocalDateTime creationDate;
}
