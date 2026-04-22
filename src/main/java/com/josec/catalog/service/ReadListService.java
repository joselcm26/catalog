package com.josec.catalog.service;

import com.josec.catalog.dto.ReadListRequestDTO;
import com.josec.catalog.dto.ReadListResponseDTO;
import com.josec.catalog.dto.mappers.ReadListMapper;
import com.josec.catalog.exception.BookNotFoundException;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.ReadList;
import com.josec.catalog.repository.BookRepository;
import com.josec.catalog.repository.ReadListRepository;
import com.josec.catalog.security.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para las listas de lectura del usuario.
 * No se pueden eliminar ni crear estas listas directamente.
 *
 * (Se crean y se eliminan con los usuarios.
 */
@Service
public class ReadListService {

    // -- DEPENDENCIAS --
    @Autowired
    private ReadListRepository readListRepository;
    @Autowired
    private ReadListMapper readListMapper;
    @Autowired
    private PermissionValidator permissionValidator;
    @Autowired
    private BookRepository bookRepository;

    // --- MÉTODOS ---

    public ReadListResponseDTO getReadList() {
        //1. Validar propietario
        Integer loggedInUserId = permissionValidator.whoIsLoggedIn();
        permissionValidator.checkPermissionByOwnerId(loggedInUserId);

        // 2. Buscar readlist
        ReadList readList = readListRepository.findByOwnerId(loggedInUserId);


        // 3. Mapear y devolver
        return readListMapper.toDTO(readList);
    }
    @Transactional
    public ReadListResponseDTO clearReadList() {
        //1. Validar propietario
        Integer loggedInUserId = permissionValidator.whoIsLoggedIn();
        permissionValidator.checkPermissionByOwnerId(loggedInUserId);

        // 2. Buscar readlist
        ReadList readList = readListRepository.findByOwnerId(loggedInUserId);

        //2. Limpiar lista
        readList.getBooks().clear();
        return readListMapper.toDTO(readList);
    }

    @Transactional
    public ReadListResponseDTO addBookToReadList(ReadListRequestDTO request) {
        //1. Validar propietario
        Integer loggedInUserId = permissionValidator.whoIsLoggedIn();
        permissionValidator.checkPermissionByOwnerId(loggedInUserId);

        // 2. Buscar readlist
        ReadList readList = readListRepository.findByOwnerId(loggedInUserId);

        //3. Buscar libro
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with Id" + request.getBookId()));

        //4. Añadir, mapear y devolver
        readList.addBook(book);
        return readListMapper.toDTO(readList);
    }

    @Transactional
    public ReadListResponseDTO removeBookFromReadList(ReadListRequestDTO request) {
        //1. Validar propietario
        Integer loggedInUserId = permissionValidator.whoIsLoggedIn();
        permissionValidator.checkPermissionByOwnerId(loggedInUserId);

        // 2. Buscar readlist
        ReadList readList = readListRepository.findByOwnerId(loggedInUserId);

        //3. Buscar libro
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with Id" + request.getBookId()));

        //4. Borrar libro, mapear y devolver
        readList.removeBook(book);
        return readListMapper.toDTO(readList);
    }

}
