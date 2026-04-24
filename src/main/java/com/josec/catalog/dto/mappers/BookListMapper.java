package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.BookListRequestDTO;
import com.josec.catalog.dto.BookListResponseDTO;
import com.josec.catalog.model.BookList;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookListMapper {

    BookListResponseDTO toDTO(BookList bookList);

    BookList toEntity(BookListRequestDTO bookListDTO);
}
