package com.josec.catalog.config;

import com.josec.catalog.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    // 1. EL ENCRIPTADOR: BCrypt

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. REGLAS DE ENTRADA

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Desactivamos CSRF porque no usamos cookies de sesion, sino tokens
        http.csrf(csrf -> csrf.disable())
                //Sin sesiones en memoria
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                //Configuración de quién entra y quién no
                .authorizeHttpRequests(auth -> auth
                        //Todos pueden ver el catálogo de libros
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        //Crear, modificar, borrar libros y portadas solo ADMIN (el prefijo no se pone)
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                        //Todos pueden ver listas
                        .requestMatchers(HttpMethod.GET, "/api/lists/**").permitAll()
                        //Todos pueden registrarse
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        //Todos podrán hacer login
                        .requestMatchers("/api/auth/**").permitAll()
                        //Logout requiere autenticación
                        .requestMatchers("/api/auth/logout").authenticated()
                        //Endpoint de errores
                        .requestMatchers("/error").permitAll()
                        // Consola
                        .requestMatchers("/h2-console/**").permitAll()
                        //CUALQUIER otra petición (crear libros, crear reseñas,...) requiere estar logueado
                        .anyRequest().authenticated()
                )
                //Desactivar iframes para que funcione la consola
                .headers(headers -> headers.
                        frameOptions(frameOptionsConfig -> frameOptionsConfig.disable() ));

        // Ponemos nuestro filtro antes que el standard de Spring
        http.addFilterBefore(jwtFilter,
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }


}
