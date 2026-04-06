package com.josec.catalog.service;

import com.josec.catalog.dto.BookListRequestDTO;
import com.josec.catalog.dto.BookListResponseDTO;
import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.UserResponseDTO;
import com.josec.catalog.exception.BookNotFoundException;
import com.josec.catalog.exception.CollaboratorAlreadyAddedException;
import com.josec.catalog.exception.ListNotFoundException;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.BookList;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.BookListRepository;
import com.josec.catalog.repository.BookRepository;
import com.josec.catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servicio CRUD para las listas de libros
 */
@Service
public class BookListService {

    // --- Dependencias ---

    @Autowired
    private BookListRepository bookListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    // --- Métodos principales ---

    /**
     * Para crear una lista nueva
     *
     * @param bookListRequestDTO con los datos de la lista a crear
     * @return BookLesResponseDTO con los datos de la lista creada
     */
    public BookListResponseDTO createList(BookListRequestDTO bookListRequestDTO) {
        // 1. Traducir los datos básicos
        BookList bookList = mapToEntity(bookListRequestDTO);

        // 2. ¡NUEVO! Leemos quién es el usuario logueado en este momento
        String loggedInUsername = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User owner = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + loggedInUsername));

        // 3. Asignamos al creador como dueño de la lista antes de guardar
        bookList.setOwner(owner);
        bookListRepository.save(bookList);
        return mapToDTO(bookList);
    }

    /**
     * Para obtener una lista de libros por su ID de la BD
     *
     * @param id de la lista
     * @return BookListResponseDTO con los datos de la lista pedida
     */
    public BookListResponseDTO getBookList(int id) {
        BookList bookList = bookListRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException(("List not found with Id: " + id)));
        return mapToDTO(bookList);
    }

    /**
     * Para eliminar una lista con el Id especificado
     *
     * @param id de la lista a eliminar
     * @return BookListResponseDTO lista eliminada
     */
    public BookListResponseDTO deleteBookList(int id) {
        BookList bookList = bookListRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException(("List not found with Id: " + id)));
        bookListRepository.delete(bookList);
        return mapToDTO(bookList);
    }

    /**
     * Añade el libro con bookId a la lista con listId
     *
     * @param listId Id de la lista
     * @param bookId Id del libro
     * @return BookResponseDTO con los datos de la lista actualizada
     */
    public BookListResponseDTO addBookToList(int listId, int bookId) {
        //Buscar libro
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(("Book not found with Id: " + bookId)));

        //Buscar lista
        BookList bookList = bookListRepository.findById(listId)
                .orElseThrow(() -> new ListNotFoundException(("List not found with Id: " + listId)));

        //Añadir libro y guardar lista
        List<Book> list = bookList.getBooks();
        list.add(book);
        bookListRepository.save(bookList);
        return mapToDTO(bookList);
    }

    public BookListResponseDTO addCollaboratorToList(int listId, int userId) {
        // 1. Buscar lista
        BookList bookList = bookListRepository.findById(listId)
                .orElseThrow(() -> new ListNotFoundException(("List not found with Id: " + listId)));

        // --- BARRERA DE SEGURIDAD (AUTORIZACIÓN) ---
        // Extraemos quién está haciendo la petición desde su Token
        String loggedInUsername = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();

        // Si el que hace la petición no es el dueño de la lista, lanzamos error 403 Forbidden
        if (!bookList.getOwner().getUsername().equals(loggedInUsername)) {
            throw new RuntimeException("Access denied: Only the owner can add collaborators");
        }

        // 2. Buscar usuario
        User user = userRepository.findById((long) userId)
                .orElseThrow(() -> new UserNotFoundException(("User not found with Id: " + userId)));

        // 3. Añadir colaborador y guardar
        List<User> collaborators = bookList.getCollaborators();

        if(Objects.equals(bookList.getOwner().getId(), user.getId())) { // Comprobar que el propietario no se añada
            throw new RuntimeException("Owner cannot add himself as collaborator");
        }

        if (collaborators.contains(user)) { // Comprobar si ya está añadido
            throw new CollaboratorAlreadyAddedException("Collaborator is already added in this list");
        }

        collaborators.add(user);
        bookListRepository.save(bookList);
        return mapToDTO(bookList);
    }

    /**
     * Borra el libro con bookId a la lista con listId
     *
     * @param listId Id de la lista
     * @param bookId Id del libro
     * @return BookResponseDTO con los datos de la lista actualizada
     */
    public BookListResponseDTO deleteBookFromList(int listId, int bookId) {
        //Buscar lista
        BookList bookList = bookListRepository.findById(listId)
                .orElseThrow(() -> new ListNotFoundException(("List not found with Id: " + listId)));

        //Buscar y borrar libro de la lista.
        List<Book> list = bookList.getBooks();
        list.removeIf(book -> book.getId() == bookId);
        bookListRepository.save(bookList);
        return mapToDTO(bookList);
    }


    // --- Métodos funcionales ---

    /**
     * Mapea entidades BookList a DTO
     *
     * @param bookList para mapear
     * @return BookListResponseDTO mapeado
     */
    private BookListResponseDTO mapToDTO(BookList bookList) {
        BookListResponseDTO dto = new BookListResponseDTO();
        dto.setId(bookList.getId());
        dto.setName(bookList.getName());
        dto.setDescription(bookList.getDescription());

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
    private BookList mapToEntity(BookListRequestDTO bookListRequestDTO) {

        BookList bookList = new BookList();
        bookList.setName(bookListRequestDTO.getName());
        bookList.setDescription(bookListRequestDTO.getDescription());
        return bookList;

    }
}
