package com.josec.catalog.controller;

import com.josec.catalog.dto.MediaLogResponseDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.service.FeedInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
public class FeedInteractionController {

    @Autowired
    private FeedInteractionService feedInteractionService;

    // --- FEEDS ---

    /**
     * Obtener el diario de logs del usuario logeado
     *
     * @param page num. pagina
     * @param size tamaño de página
     * @return lista de logs propios del usuario
     */
    @GetMapping("/diary")
    public ResponseEntity<Page<MediaLogResponseDTO>> getMyReadingLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(feedInteractionService.getMyDiary(page, size));
    }

    /**
     * Obtener el home feed del usuario. Se obtienen logs de amigos (usuarios seguidos)
     *
     * @param page num. pagina
     * @param size tamaño de página
     * @return lista de logs de usuarios seguidos
     */
    @GetMapping("/home")
    public ResponseEntity<Page<MediaLogResponseDTO>> getHomeFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(feedInteractionService.getMyHomeFeed(page, size));
    }

    /**
     * Obtener el feed de exploracion (logs públicos de tödo el mundo
     *
     * @param page num. pagina
     * @param size tamaño de pagina
     * @return lista de los públicos
     */
    @GetMapping("/explore")
    public ResponseEntity<Page<MediaLogResponseDTO>> getExploreFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(feedInteractionService.getExploreFeed(page, size));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Integer id) {
        feedInteractionService.toggleLike(id);
        return ResponseEntity.ok("Like " + id + " has been toggled");
    }
}
