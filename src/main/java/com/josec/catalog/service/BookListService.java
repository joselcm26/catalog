package com.josec.catalog.service;

import com.josec.catalog.dto.*;
import com.josec.catalog.dto.mappers.BookListMapper;
import com.josec.catalog.dto.mappers.BookMapper;
import com.josec.catalog.exception.*;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.BookList;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.BookListRepository;
import com.josec.catalog.repository.BookRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookListMapper bookListMapper;

    @Autowired
    private PermissionValidator permissionValidator;

    // --- Métodos principales ---

    /**
     * Para crear una lista nueva
     *
     * @param bookListRequestDTO con los datos de la lista a crear
     * @return BookLesResponseDTO con los datos de la lista creada
     */
    public BookListResponseDTO createList(BookListRequestDTO bookListRequestDTO) {
        // 1. Traducir los datos básicos
        BookList bookList = bookListMapper.mapToEntity(bookListRequestDTO);

        // 2. Leemos quién es el usuario logueado en este momento
        Integer loggedInUserId = permissionValidator.whoIsLoggedIn();

        User owner = null;
        if (loggedInUserId != null) {
            owner = userRepository.findById(loggedInUserId.longValue())
                    .orElseThrow(() -> new RuntimeException("User not found: " + loggedInUserId));
        }

        // 3. Asignamos al creador como dueño de la lista antes de guardar
        bookList.setOwner(owner);
        bookListRepository.save(bookList);
        return bookListMapper.mapToDTO(bookList);
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

        //Comprobación de seguridad
        // Si la lista es PRIVADA, comprobamos si el que mira es el dueño
        if (!bookList.isPublic()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Si el usuario no ha enviado Token (es null), o no es el dueño, le bloqueamos
            if (auth == null || !bookList.getOwner().getUsername().equals(auth.getName())) {
                throw new RuntimeException("Access denied: This list is private");
            }
        }

        return bookListMapper.mapToDTO(bookList);
    }

    public List<BookResponseDTO> searchBooksInMyList(int listId, String query) {
        // 1. Comprobar lista
        BookList bookList = bookListRepository.findById(listId)
                .orElseThrow(() -> new ListNotFoundException(("List not found with Id: " + listId)));

        // 2. Comprobar permisos
        checkPermissions(bookList);

        // 3. Hacer búsqueda
        List<Book> foundBooks = bookRepository.searchBooksInsideList(listId, query);

        // 4. Devolver los DTOs
        return foundBooks.stream().map(bookMapper::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Obtiene las listas creadas por el usuario
     *
     * @return List<BookListResponseDTO>
     */
    public List<BookListResponseDTO> getMyLists() {
        // 1. Sacar ID de usuario del token
        Integer loggedInUserId = permissionValidator.whoIsLoggedIn();

        //3. Buscar las listas del usuario
        List<BookList> userLists = bookListRepository.findByOwnerId(loggedInUserId);

        return userLists.stream()
                .map(bookListMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Para eliminar una lista con el Id especificado
     *
     * @param id de la lista a eliminar
     * @return BookListResponseDTO lista eliminada
     */
    @Transactional
    public void deleteBookList(int id) {
        BookList bookList = bookListRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException(("List not found with Id: " + id)));

        // Comprobar dueño
        checkOwner(bookList);

        bookListRepository.delete(bookList);

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

        //Seguridad - comprobar si dueño o colaborador
        checkPermissions(bookList);

        //Comprobar duplicados
        if(bookList.getBooks().contains(book)) {
            throw new RuntimeException("Book already exists in the list");
        }

        //Añadir libro y guardar lista
        List<Book> list = bookList.getBooks();
        list.add(book);
        bookListRepository.save(bookList);
        return bookListMapper.mapToDTO(bookList);
    }

    public BookListResponseDTO addCollaboratorToList(int listId, int userId) {
        // 1. Buscar lista
        BookList bookList = bookListRepository.findById(listId)
                .orElseThrow(() -> new ListNotFoundException(("List not found with Id: " + listId)));

        // --- BARRERA DE SEGURIDAD (AUTORIZACIÓN) ---
        // Extraemos quién está haciendo la petición desde su Token
        checkOwner(bookList);

        //Comprobar si la lista es pública

        if(!bookList.isPublic()){
            throw new RuntimeException("Cannot add collaborators to a private list");
        }

        // 2. Buscar usuario
        User user = userRepository.findById((long) userId)
                .orElseThrow(() -> new UserNotFoundException(("User not found with Id: " + userId)));

        // 3. Añadir colaborador y guardar
        List<User> collaborators = bookList.getCollaborators();

        if(Objects.equals(bookList.getOwner().getId(), user.getId())) { // Comprobar que el propietario no se añada
            throw new AccessDeniedException("Owner cannot add himself as collaborator");
        }

        if (collaborators.contains(user)) { // Comprobar si ya está añadido
            throw new CollaboratorAlreadyAddedException("Collaborator is already added in this list");
        }

        collaborators.add(user);
        bookListRepository.save(bookList);
        return bookListMapper.mapToDTO(bookList);
    }

    public BookListResponseDTO deleteCollaboratorFromList(int listId, int userId) {
        // 1. Buscar lista
        BookList bookList = bookListRepository.findById(listId)
                .orElseThrow(() -> new ListNotFoundException(("List not found with Id: " + listId)));

        // --- BARRERA DE SEGURIDAD (AUTORIZACIÓN) ---
        checkOwner(bookList);

        // 2. Buscar usuario
        User user = userRepository.findById((long) userId)
                .orElseThrow(() -> new UserNotFoundException(("User not found with Id: " + userId)));

        // 3. Eliminar usuario de la lista
        List<User> collaborators = bookList.getCollaborators();

        if(Objects.equals(bookList.getOwner().getId(), user.getId())) { // Comprobar que el propietario no se elimine
            throw new AccessDeniedException("Owner cannot delete himself as collaborator");
        }

        if (!collaborators.contains(user)) { // Comprobar si está añadido
            throw new CollaboratorAlreadyAddedException("Collaborator is not added in this list");
        }

        collaborators.remove(user);
        bookListRepository.save(bookList);
        return bookListMapper.mapToDTO(bookList);
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

        //Seguridad
        checkPermissions(bookList);

        //Buscar y borrar libro de la lista.
        List<Book> list = bookList.getBooks();
        list.removeIf(book -> book.getId() == bookId);
        bookListRepository.save(bookList);
        return bookListMapper.mapToDTO(bookList);
    }

    // -- GESTIÓN DE PAPELERA --

    public List<BookListResponseDTO> getMyTrash(){
        Integer ownerId = permissionValidator.whoIsLoggedIn();

        List<BookList> bookList = bookListRepository.findAllDeletedByUserId(ownerId);

        if(!bookList.isEmpty()) {
            return bookList.stream()
                    .map(bookListMapper::mapToDTO)
                    .toList();
        }
        else {
            throw new EmptyReadingLogException("The book list trash for this user is empty");
        }
    }

    @Transactional
    public BookListResponseDTO restoreBookList(int logId) {
        // 1. Buscar en la papelera
        BookList bookList = bookListRepository.findDeletedById(logId);

        // 2. Validar al usuario
        permissionValidator.checkPermissions(bookList);

        // 3. Restaurar (poner fecha de borrado a null)
        bookList.setDeletedAt(null);
        BookList restoredBookList = bookListRepository.save(bookList);

        return  bookListMapper.mapToDTO(restoredBookList);
    }


    // --- Métodos funcionales ---

    //TODO: mover estos dos métodos a una clase propia (cuando haya listas de más cosas serán útiles)
    /**
     * Métôdo privado de apoyo para verificar si el usuario actual
     * tiene permiso para editar la lista (dueño o colaborador).
     */
    private void checkPermissions(BookList bookList) {
        //Obtener usuario loggeado
        String loggedInUsername = Objects
                .requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication()).getName();

        //Es dueño?
        boolean isOwner = bookList.getOwner().getUsername().equals(loggedInUsername);

        //Es colaborador?
        boolean isCollaborator = bookList.getCollaborators().stream()
                .anyMatch(c -> c.getUsername().equals(loggedInUsername));

        // Comprobaciones
        if (!bookList.isPublic() && !isOwner) {
            // Si la lista es privada, NADIE excepto el dueño puede siquiera tocarla
            throw new AccessDeniedException("This list is private.");
        }

        if (!isOwner && !isCollaborator) {
            throw new AccessDeniedException("You don't have permission to edit this list.");
        }
    }

    private void checkOwner(BookList bookList) {
        // --- BARRERA DE SEGURIDAD (AUTORIZACIÓN) ---
        // Extraemos quién está haciendo la petición desde su Token
        String loggedInUsername = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();

        // Si el que hace la petición no es el dueño de la lista, lanzamos error 403 Forbidden
        if (!bookList.getOwner().getUsername().equals(loggedInUsername)) {
            throw new AccessDeniedException("Access denied: Only the owner can do this operation");
        }
    }
}
