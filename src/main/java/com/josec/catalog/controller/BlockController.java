package com.josec.catalog.controller;

import com.josec.catalog.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para bloquear y desbloquear usuarios
 */
@RestController
@RequestMapping("/api/users")
public class BlockController {

    @Autowired
    private BlockService blockService;

    @PostMapping("/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Integer targetUser) {
        blockService.blockUser(targetUser);
        return ResponseEntity.ok("User with id " + targetUser + " has been blocked");
    }

    @DeleteMapping("/{id}/block")
    public ResponseEntity<String> unblockUser(@PathVariable Integer targetUser) {
        blockService.unblockUser(targetUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
