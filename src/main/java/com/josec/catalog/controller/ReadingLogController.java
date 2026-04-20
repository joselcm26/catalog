package com.josec.catalog.controller;

import com.josec.catalog.dto.ReadingLogRequestDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.service.ReadingLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reading-log")
public class ReadingLogController {

    // --- DEPENDENCIAS ---

    @Autowired
    private ReadingLogService readingLogService;

    // --- CRUD BÁSICO ---

    /**
     * Obtener read logs del usuario logeado
     * @return resultado
     */
    @GetMapping
    public ResponseEntity<List<ReadingLogResponseDTO>> getMyReadingLogs() {
        return ResponseEntity.ok(readingLogService.getMyReadingLogs());
    }

    /**
     * Postear un read log en el log del usuario logeado
     * @param requestDTO con los datos requeridos
     * @return resultado
     */
    @PostMapping
    public ResponseEntity<ReadingLogResponseDTO> postReadingLog(@Valid @RequestBody ReadingLogRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readingLogService.logBookAsRead(requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReadingLog(@PathVariable Integer id) {
        readingLogService.deleteReadingLog(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    // -- PAPELERA ---

    @GetMapping("/trash")
    public ResponseEntity<List<ReadingLogResponseDTO>> getTrash() {
        return ResponseEntity.ok(readingLogService.getMyTrash());
    }

    @PatchMapping("/trash/{id}/restore")
    public ResponseEntity<?> restoreTrashItem(@PathVariable Integer id) {
        return ResponseEntity.ok(readingLogService.restoreReadingLog(id));
    }
}
