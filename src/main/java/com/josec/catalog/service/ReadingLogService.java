package com.josec.catalog.service;

import com.josec.catalog.dto.ReadingLogRequestDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.dto.mappers.ReadingLogMapper;
import com.josec.catalog.exception.BookNotFoundException;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.ReadList;
import com.josec.catalog.model.ReadingLog;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.BookRepository;
import com.josec.catalog.repository.ReadListRepository;
import com.josec.catalog.repository.ReadingLogRepository;
import com.josec.catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
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

    @Transactional
    public ReadingLogResponseDTO logBookAsRead(ReadingLogRequestDTO request){
        // 1. Extraer el Id de usuario
        Integer userId = (Integer) Objects
                .requireNonNull(Objects.
                                requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getDetails());
        User user = userRepository.findById(userId.longValue()).orElseThrow();

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
        // Buscamos la readlist del usuario (asumiendo que tienes un findByUserId en el repositorio)
        Optional<ReadList> optionalReadList = Optional.ofNullable(readListRepository.findByUserId(user.getId()));

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
        return  readingLogMapper.mapToDTO(savedLog);
    }
}
