package com.josec.catalog.dto;

import com.josec.catalog.model.Book;
import lombok.Data;

import java.util.List;

@Data
public class ReadListResponseDTO {

    private Integer id;
    private Integer ownerId;
    private List<BookResponseDTO> books;
}
