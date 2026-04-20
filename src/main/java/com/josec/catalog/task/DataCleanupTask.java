package com.josec.catalog.task;

import com.josec.catalog.repository.BookListRepository;
import com.josec.catalog.repository.ReadingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Componente limpiador de elementos eliminados de la base de datos.
 * Elimina elementos ReadingLog y BookList marcados como eliminados después de 30 días.
 */
@Component
public class DataCleanupTask {
    @Autowired
    private ReadingLogRepository readingLogRepository;
    @Autowired
    private BookListRepository bookListRepository;

    // Se ejecuta cada día a las 03:00 AM
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void permanentDeleteOldRecords() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);

        // Ejecutamos un borrado físico real (HARD DELETE) de lo que sea muy viejo
        readingLogRepository.deletePermanentlyOlderThan(threshold);
        bookListRepository.deletePermanentlyOlderThan(threshold);

        System.out.println("Limpieza de papelera completada para registros anteriores a: " + threshold);
    }
}
