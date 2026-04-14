package com.josec.catalog.controller;

import com.josec.catalog.dto.ReadingLogRequestDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.service.ReadingLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reading-log")
public class ReadingLogController {

    // --- DEPENDENCIAS ---

    @Autowired
    private ReadingLogService readingLogService;

    // --- CRUD BÁSICO ---

    @PostMapping
    public ResponseEntity<ReadingLogResponseDTO> postReadingLog(@RequestBody ReadingLogRequestDTO requestDTO) {
        return ResponseEntity.ok(readingLogService.logBookAsRead(requestDTO));
    }
}
