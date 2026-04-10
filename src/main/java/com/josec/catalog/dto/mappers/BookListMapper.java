package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.BookListRequestDTO;
import com.josec.catalog.dto.BookListResponseDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.UserResponseDTO;
import com.josec.catalog.model.BookList;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookListMapper {

    /**
     * Mapea entidades BookList a DTO
     *
     * @param bookList para mapear
     * @return BookListResponseDTO mapeado
     */
    public BookListResponseDTO mapToDTO(BookList bookList) {
        BookListResponseDTO dto = new BookListResponseDTO();
        dto.setId(bookList.getId());
        dto.setName(bookList.getName());
        dto.setDescription(bookList.getDescription());
        dto.setPublic(bookList.isPublic());

        // 1. Mapear el dueño
        if(bookList.getOwner() != null) {
            dto.setOwnerID(bookList.getOwner().getId());
            dto.setOwnerUsername(bookList.getOwner().getUsername());
        }

        // 2. Mapear colaboradores
        if(bookList.getCollaborators() != null) {
            List<UserResponseDTO> collaborators = bookList.getCollaborators().stream().map(user -> {
                UserResponseDTO userDTO = new UserResponseDTO();
                userDTO.setId(user.getId().longValue());
                userDTO.setUsername(user.getUsername());
                userDTO.setEmail(user.getEmail());
                return userDTO;
            }).collect(Collectors.toList());
            dto.setCollaborators(collaborators);}

        // 2. Mapear libros

        if(bookList.getBooks() != null) {
            List<BookResponseDTO> books = bookList.getBooks().stream().map(book -> {
                BookResponseDTO bookDTO = new BookResponseDTO();
                bookDTO.setId(book.getId().intValue());
                bookDTO.setTitle(book.getTitle());
                bookDTO.setAuthor(book.getAuthor());
                bookDTO.setPublicationYear(book.getPublicationYear());
                bookDTO.setSynopsis(book.getSynopsis());
                // No se devuelven aquí las reseñas, no son necesarias
                return bookDTO;
            }).collect(Collectors.toList());
            dto.setBooks(books);
        }
        return dto;
    }

    /**
     * Mapea DTO a una entidad BookList
     *
     * @param bookListRequestDTO para mapear
     * @return BookList mapeado
     */
    public BookList mapToEntity(BookListRequestDTO bookListRequestDTO) {

        BookList bookList = new BookList();
        bookList.setName(bookListRequestDTO.getName());
        bookList.setDescription(bookListRequestDTO.getDescription());
        bookList.setPublic(bookListRequestDTO.isPublic());
        return bookList;

    }
}
