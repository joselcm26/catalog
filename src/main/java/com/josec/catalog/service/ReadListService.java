package com.josec.catalog.service;

import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.ReadListRequestDTO;
import com.josec.catalog.dto.ReadListResponseDTO;
import com.josec.catalog.dto.mappers.ReadListMapper;
import com.josec.catalog.exception.AccessDeniedException;
import com.josec.catalog.exception.BookNotFoundException;
import com.josec.catalog.exception.ReadListNotFoundException;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.ReadList;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.BookRepository;
import com.josec.catalog.repository.ReadListRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    private UserRepository userRepository;
    @Autowired
    private PermissionValidator permissionValidator;
    @Autowired
    private BookRepository bookRepository;

    // --- MÉTODOS ---

    public ReadListResponseDTO getReadListById(ReadListRequestDTO request) {
        // 1. Validar propietario
        permissionValidator.checkPermissionByOwnerId(request.getOwnerId());

        // 2. Buscar readlist
        ReadList readList = readListRepository.findById(request.getId())
                .orElseThrow(() -> new ReadListNotFoundException("ReadList not found with Id" + request.getId()));

        // 3. Mapear y devolver
        return readListMapper.mapToDTO(readList);
    }

    public ReadListResponseDTO getReadListByOwnerId(ReadListRequestDTO request) {
        //1. Validar propietario
        permissionValidator.checkPermissionByOwnerId(request.getOwnerId());

        //2. Buscar por el Id del propietario
        ReadList readList =  readListRepository.findByOwnerId(request.getId());

        //3. Mapear y devolver
        return readListMapper.mapToDTO(readList);

    }

    public ReadListResponseDTO clearReadListById(ReadListRequestDTO request) {
        //1. Validar propietario
        permissionValidator.checkPermissionByOwnerId(request.getOwnerId());

        // 2. Buscar readlist
        ReadList readList = readListRepository.findById(request.getId())
                .orElseThrow(() -> new ReadListNotFoundException("ReadList not found with Id" + request.getId()));

        //2. Limpiar lista
        readList.getBooks().clear();
        return readListMapper.mapToDTO(readList);
    }

    public ReadListResponseDTO addBookToReadList(Integer bookId, ReadListRequestDTO request) {
        //1. Validar propietario
        permissionValidator.checkPermissionByOwnerId(request.getOwnerId());

        //2. Buscar readlist
        ReadList readList = readListRepository.findById(request.getId())
                .orElseThrow(() -> new ReadListNotFoundException("ReadList not found with Id" + request.getId()));

        //3. Buscar libro
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with Id" + bookId));

        //4. Añadir, mapear y devolver
        readList.addBook(book);
        return readListMapper.mapToDTO(readList);
    }

    public ReadListResponseDTO removeBookFromReadList(Integer bookId, ReadListRequestDTO request) {
        //1. Validar propietario
        permissionValidator.checkPermissionByOwnerId(request.getOwnerId());

        //2. Buscar readlist
        ReadList readList = readListRepository.findById(request.getId())
                .orElseThrow(() -> new ReadListNotFoundException("ReadList not found with Id" + request.getId()));

        //3. Buscar libro
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with Id" + bookId));

        //4. Borrar libro, mapear y devolver
        readList.removeBook(book);
        return readListMapper.mapToDTO(readList);
    }

}
