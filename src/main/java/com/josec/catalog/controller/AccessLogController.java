package com.josec.catalog.controller;

import com.josec.catalog.dto.AccessLogResponseDTO;
import com.josec.catalog.dto.mappers.AccessLogMapper;
import com.josec.catalog.model.AccessLog;
import com.josec.catalog.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access-log")
public class AccessLogController {

    @Autowired
    private AccessLogService accessLogService;


    /**
     * Devuelve los logs de acceso del usuario logeado
     * 
     * @param page núm. página
     * @param size tamaño de página
     * @return página de logs
     */
    @GetMapping
    public ResponseEntity<Page<AccessLogResponseDTO>> getMyLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(accessLogService.getMyAccessLogs(page, size));
    }

    /**
     * SOLO PARA ADMINISTRADORES.
     *
     * Devuelve TODOS los logs de acceso, paginados.
     *
     * @param page núm. página
     * @param size tamaño de página
     * @return página de logs
     */
    @GetMapping("/all")
    public ResponseEntity<Page<AccessLogResponseDTO>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(accessLogService.getAllAccessLogs(page, size));
    }
}
