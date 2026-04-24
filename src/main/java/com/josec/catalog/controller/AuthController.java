package com.josec.catalog.controller;

import com.josec.catalog.dto.AuthRequestDTO;
import com.josec.catalog.dto.AuthResponseDTO;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.JwtUtil;
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
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO requestDTO) {

        // 1. Buscar el usuario en la base de datos
        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElse(null);
        // 2. Comprobamos si el usuario existe y si la contraseña es correcta
        if (user == null || !passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user or password");
        }

        // 3. Si tôdo es correcto, generamos el token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().toString());

        // 4. Devolver al usuario
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Devolver un 200 ok al front-end para que proceda a borrar el token de sesión.
        return ResponseEntity.ok("Successfully logged out. Please delete the token on the client side.");
    }

}
