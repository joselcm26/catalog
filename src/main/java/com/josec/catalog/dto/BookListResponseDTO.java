package com.josec.catalog.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookListResponseDTO {

    private long id;
    private String name;
    private String description;
    private boolean isPublic;
    private LocalDateTime deletedAt;

    //Datos del dueño
    private long ownerID;
    private String ownerUsername;

    //Reutilizar DTO anteriores para no enviar entidades de la BD
    private List<UserResponseDTO> collaborators;
    private List<BookResponseDTO> books;
}
