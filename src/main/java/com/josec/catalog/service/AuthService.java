package com.josec.catalog.service;

import com.josec.catalog.dto.AuthRequestDTO;
import com.josec.catalog.exception.InvalidCredentialsException;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String loginUser(AuthRequestDTO requestDTO) {
        // 1. Buscar el usuario en la base de datos
        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with username: " + requestDTO.getUsername()));
        // 2. Comprobamos si el usuario existe y si la contraseña es correcta
        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid user or password");
        }

        // 3. Si tôdo es correcto, generamos el token
        return jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().toString());
    }
}
