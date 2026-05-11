package com.josec.catalog.controller;

import com.josec.catalog.dto.*;
import com.josec.catalog.model.User;
import com.josec.catalog.security.PermissionValidator;
import com.josec.catalog.service.FileStorageService;
import com.josec.catalog.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionValidator permissionValidator;

    @Autowired
    private FileStorageService fileStorageService;

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
    @PostMapping(value = "/my/profile/image", consumes = MediaType .MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImage(@RequestParam("file") MultipartFile file) {
        // 1. Obtener Id del usuario
        Integer loggedUserId = permissionValidator.whoIsLoggedIn();

        // 2. Guardar archivo en disco y obtener nombre
        String filename = fileStorageService.saveImage(file, FileStorageService.ImageType.PROFILE);

        // 3. Actualizar y devolver

        UserResponseDTO updatedUser = userService.updateCoverImage(loggedUserId, filename);
        return ResponseEntity.ok(updatedUser);

    }

    @PatchMapping("/my/password")
    public ResponseEntity<String> changeMyPassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.changePassword(requestDTO));
    }

    @PatchMapping("/my/privacy")
    public ResponseEntity<String> changePrivacy(@Valid @RequestBody PrivacyChangeDTO requestDTO) {
        userService.changePrivacy(requestDTO.isPrivateProfile());
        return ResponseEntity.ok("Privacy has been successfully changed.");
    }
}
