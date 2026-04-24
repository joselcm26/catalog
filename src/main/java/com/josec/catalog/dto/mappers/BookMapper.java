package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.BookRequestDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    /**
     * Mapear a DTO
     *
     * @param book entidad
     * @return BookResponseDTO
     */
    BookResponseDTO toDTO(Book book);

    /**
     * Mapear a entidad
     * @param book DTO
     * @return Book
     */
    Book toEntity(BookRequestDTO book);
}
