package com.josec.catalog.service;

import com.josec.catalog.dto.AuthRequestDTO;
import com.josec.catalog.exception.InvalidCredentialsException;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.model.AccessLog;
import com.josec.catalog.model.User;
import com.josec.catalog.repository.AccessLogRepository;
import com.josec.catalog.repository.UserRepository;
import com.josec.catalog.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public String loginUser(AuthRequestDTO requestDTO, HttpServletRequest request) {
        // 1. Buscar el usuario en la base de datos
        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with username: " + requestDTO.getUsername()));
        // 2. Comprobamos si el usuario existe y si la contraseña es correcta
        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid user or password");
        }

        // 3. Extraer IP real (Sorteando posibles Proxies)
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        } else {
            // X-Forwarded-For a veces devuelve una lista de IPs separada por comas. Nos quedamos con la primera.
            ipAddress = ipAddress.split(",")[0].trim();
        }

        // 4. Extraer Dispositivo/Navegador
        String userAgent = request.getHeader("User-Agent");

        // 5. Guardar en el historial
        AccessLog log = new AccessLog();
        log.setUser(user);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setSuccess(true);

        accessLogRepository.save(log);


        // 6. Si tôdo es correcto, generamos el token
        return jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().toString());
    }
}
