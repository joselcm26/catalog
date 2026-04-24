package com.josec.catalog.controller;

import com.josec.catalog.dto.AuthRequestDTO;
import com.josec.catalog.dto.AuthResponseDTO;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.JwtUtil;
import com.josec.catalog.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // --- DEPENDENCIAS ---

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO requestDTO) {

        String token = authService.loginUser(requestDTO);

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Devolver un 200 ok al front-end para que proceda a borrar el token de sesión.
        return ResponseEntity.ok("Successfully logged out. Please delete the token on the client side.");
    }

}
