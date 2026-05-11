package com.josec.catalog.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    // --- Dependencias ---

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Mirar si la petición trae la cabecera "Authorization"
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Comprobar que la cabecera existe y que empieza por "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);// Recortar 7 primeros caracteres
            try {
                username = jwtUtil.extractUsername(token); // Sacar nombre
            } catch (Exception e) {
                System.out.println("Error leyendo token");
            }
        }

        // 3. Si hemos sacado un nombre y el usuario aún no está autenticado en este hilo...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Comprobamos validez del token
            if(jwtUtil.validateToken(token)) {
                // 1. Extraer el ID y el rol del token
                int userId = jwtUtil.extractUserId(token);
                String userRole = jwtUtil.extractRole(token);

                // 2. Crear acreditación. Decir a Spring Security que este usuario está autenticado
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole)); // Poner prefijo ROLE_

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);

                // 3. Guardar ID en los detalles de la acreditación
                authToken.setDetails(userId);
                
                // 4. Oficializar autenticación
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 4. Pase lo que pase, le decimos a la peticion que continúe su camino hacia el controlador
        filterChain.doFilter(request, response);
    }
}
