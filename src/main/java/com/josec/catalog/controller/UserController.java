package com.josec.catalog.controller;

import com.josec.catalog.dto.UserProfileUpdateRequestDTO;
import com.josec.catalog.dto.UserRequestDTO;
import com.josec.catalog.dto.UserResponseDTO;
import com.josec.catalog.security.PermissionValidator;
import com.josec.catalog.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionValidator permissionValidator;

    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.registerUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/my/profile")
    public ResponseEntity<UserResponseDTO> getUserInfo(){
        return ResponseEntity.ok(userService.getMyUserInfo());
    }

    @PatchMapping("/my/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequestDTO userRequestDTO) {

        // 1. Validar usuario y extraer token
        Integer loggedUserId = permissionValidator.whoIsLoggedIn();

        // 2. Llamamos al servicio para que actualice los datos

        UserResponseDTO updatedUser = userService.updateUser(loggedUserId, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }
}
