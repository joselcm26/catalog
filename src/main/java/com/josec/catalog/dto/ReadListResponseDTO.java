package com.josec.catalog.dto;

import com.josec.catalog.model.Book;
import lombok.Data;

import java.util.List;

@Data
public class ReadListResponseDTO {

    private List<BookResponseDTO> books;
}
