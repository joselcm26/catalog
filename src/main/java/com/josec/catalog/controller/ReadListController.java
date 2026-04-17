package com.josec.catalog.controller;

import com.josec.catalog.dto.ReadListRequestDTO;
import com.josec.catalog.dto.ReadListResponseDTO;
import com.josec.catalog.service.ReadListService;
import jakarta.validation.Valid;
import lombok.Locked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/readlist")
public class ReadListController {

    @Autowired
    private ReadListService readListService;

    @GetMapping
    public ResponseEntity<ReadListResponseDTO> getReadList() {
        return ResponseEntity.ok(readListService.getReadList());
    }

    @PostMapping
    public ResponseEntity<ReadListResponseDTO> addBookToMyReadList(@Valid @RequestBody ReadListRequestDTO request) {
        return ResponseEntity.ok(readListService.addBookToReadList(request));
    }

    @DeleteMapping
    public ResponseEntity<?> removeBookFromMyReadList(@Valid @RequestBody ReadListRequestDTO request) {
        readListService.removeBookFromReadList(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ReadListResponseDTO> clearReadList() {
        readListService.clearReadList();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
