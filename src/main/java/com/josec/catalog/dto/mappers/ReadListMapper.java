package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.ReadListRequestDTO;
import com.josec.catalog.dto.ReadListResponseDTO;
import com.josec.catalog.model.ReadList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReadListMapper {

    @Autowired
    private BookMapper bookMapper;

    public ReadListResponseDTO mapToDTO(ReadList readList) {
        ReadListResponseDTO dto = new ReadListResponseDTO();

        // Mapear lista a DTO
        List<BookResponseDTO> books = readList.getBooks().stream().map(book -> {
            BookResponseDTO dtoBook = new BookResponseDTO();
            dtoBook = bookMapper.mapToDTO(book);
            return dtoBook;
        }).toList();

        dto.setBooks(books);
        return dto;
    }

//    public ReadList mapToReadList(ReadListRequestDTO requestDTO) {
//        ReadList readList = new ReadList();
//        readList.setId(requestDTO.getId());
//
//        return  readList;
//
//    }
}
