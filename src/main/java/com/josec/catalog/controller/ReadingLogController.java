package com.josec.catalog.controller;

import com.josec.catalog.dto.ReadingLogRequestDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.service.ReadingLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class ReadingLogController {

    // --- DEPENDENCIAS ---

    @Autowired
    private ReadingLogService readingLogService;

    // --- FEEDS ---

    /**
     * Obtener el diario de logs del usuario logeado
     *
     * @param page num. pagina
     * @param size tamaño de página
     * @return lista de logs propios del usuario
     */
    @GetMapping("/diary")
    public ResponseEntity<Page<ReadingLogResponseDTO>> getMyReadingLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(readingLogService.getMyDiary(page, size));
    }

    /**
     * Obtener el home feed del usuario. Se obtienen logs de amigos (usuarios seguidos)
     *
     * @param page num. pagina
     * @param size tamaño de página
     * @return lista de logs de usuarios seguidos
     */
    @GetMapping("/home")
    public ResponseEntity<Page<ReadingLogResponseDTO>> getHomeFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(readingLogService.getMyHomeFeed(page, size));
    }

    /**
     * Obtener el feed de exploracion (logs públicos de tödo el mundo
     *
     * @param page num. pagina
     * @param size tamaño de pagina
     * @return lista de los públicos
     */
    @GetMapping("/explore")
    public ResponseEntity<Page<ReadingLogResponseDTO>> getExploreFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(readingLogService.getExploreFeed(page, size));
    }

    // --- GESTIÓN DE LOGS ---

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
