package com.josec.catalog.service;

import com.josec.catalog.dto.ReadingLogRequestDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.dto.mappers.ReadingLogMapper;
import com.josec.catalog.exception.*;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.ReadList;
import com.josec.catalog.model.ReadingLog;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.BookRepository;
import com.josec.catalog.repository.ReadListRepository;
import com.josec.catalog.repository.ReadingLogRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




import java.util.List;
import java.util.Optional;

@Service
public class ReadingLogService {

    // --- DEPENDENCIAS ---

    @Autowired
    private ReadingLogRepository readingLogRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReadListRepository readListRepository;
    @Autowired
    private ReadingLogMapper readingLogMapper;
    @Autowired
    private PermissionValidator permissionValidator;


    // TODO: Vista filtrada: Solo libros
//    public Page<ReadingLogResponseDTO> getMyReadingDiary(int page, int size) {
//        // Usa ReadingLogRepository para traer SOLO libros
//    }

    // -- LOGS --

    @Transactional
    public ReadingLogResponseDTO logBookAsRead(ReadingLogRequestDTO request){
        // 1. Extraer el Id de usuario
        Integer userId = permissionValidator.whoIsLoggedIn();

        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Buscar el libro
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + request.getBookId()));

        // 3. Crear el registro
        ReadingLog log = new ReadingLog();
        log.setBook(book);
        log.setUser(user);
        log.setReadDate(request.getReadDate());
        log.setRating(request.getRating());
        log.setPrivateComment(request.getPrivateComment());



        // 4. ELIMINAR DE READLIST (Si está)
        // Buscamos la readlist del usuario
        Optional<ReadList> optionalReadList = Optional.ofNullable(readListRepository.findByOwnerId(userId));

        if (optionalReadList.isPresent()) {
            ReadList readlist = optionalReadList.get();
            // Comprobamos si el libro estaba en "Pendientes"
            if (readlist.getBooks().contains(book)) {
                readlist.removeBook(book); // Usamos helper method
                readListRepository.save(readlist); // Actualizamos la base de datos
            }
        }

        // GUARDAR
        ReadingLog savedLog = readingLogRepository.save(log);
        return  readingLogMapper.toDTO(savedLog);
    }

    @Transactional
    public void deleteReadingLog(int logId) {
        // 1. Extraer el Id de usuario
        permissionValidator.whoIsLoggedIn();

        // 2. Buscar reading log
        ReadingLog log = readingLogRepository.findById(logId)
                .orElseThrow(() -> new ReadingLogNotFoundException("Reading log not found with ID: " + logId));

        // 3. Comprobar autorización
        permissionValidator.checkPermissions(log);

        // 4. Borrar y devolver
        readingLogRepository.delete(log);

    }

    // -- GESTIÓN DE PAPELERA --

    public List<ReadingLogResponseDTO> getMyTrash(){
        Integer ownerId = permissionValidator.whoIsLoggedIn();

        List<ReadingLog> log = readingLogRepository.findAllDeletedByUserId(ownerId);

        if(!log.isEmpty()) {
            return log.stream()
                    .map(readingLogMapper::toDTO)
                    .toList();
        }
        else {
            throw new EmptyReadingLogException("The reading log trash for this user is empty");
        }
    }

    @Transactional
    public ReadingLogResponseDTO restoreReadingLog(int logId) {
        // 1. Buscar en la papelera
        ReadingLog log = readingLogRepository.findDeletedById(logId);

        // 2. Validar al usuario
        permissionValidator.checkPermissions(log);

        // 3. Restaurar (poner fecha de borrado a null)
        log.setDeletedAt(null);
        ReadingLog restoredLog = readingLogRepository.save(log);

        return  readingLogMapper.toDTO(restoredLog);
    }
}
