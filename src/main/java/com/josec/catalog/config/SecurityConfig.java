package com.josec.catalog.config;

import com.josec.catalog.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
        // Desactivamos CSRF porque no usamos cookies de sesión, sino tokens
        http
                .cors(Customizer.withDefaults()) // Aplicar configuración de WebConfig para CORS
                .csrf(csrf -> csrf.disable())
                //Sin sesiones en memoria
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                //Configuración de quién entra y quién no
                .authorizeHttpRequests(auth -> auth
                                // 1. RUTAS PÚBLICAS
                                //Todos pueden ver las subidas
                                .requestMatchers("/uploads/**").permitAll()
                                //Todos podrán hacer login
                                .requestMatchers("/api/auth/**").permitAll()
                                //Endpoint de errores
                                .requestMatchers("/error").permitAll()
                                //Todos pueden ver el catálogo de libros
                                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                                //Todos pueden ver listas
                                .requestMatchers(HttpMethod.GET, "/api/lists/**").permitAll()
                                //Todos pueden registrarse
                                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                                //Permisos para Swagger
                                // Rutas de Swagger/OpenAPI
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
//                        // Consola
//                        .requestMatchers("/h2-console/**").permitAll()

                                // 2. RUTAS CON AUTENTICACIÓN
                                //Crear, modificar, borrar libros y portadas solo ADMIN (el prefijo no se pone)
                                .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/api/books/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                                //Logout requiere autenticación
                                .requestMatchers("/api/auth/logout").authenticated()
                                //CUALQUIER otra petición (crear libros, crear reseñas,...) requiere estar logueado
                                .anyRequest().authenticated()
                );
        //Desactivar iframes para que funcione la consola
//                .headers(headers -> headers.
//                        frameOptions(frameOptionsConfig -> frameOptionsConfig.disable() ));

        // Ponemos nuestro filtro antes que el standard de Spring
        http.addFilterBefore(jwtFilter,
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Le decimos a Spring Security que IGNORE por completo esta ruta.
        // Ni filtros, ni JWT, ni nada. Vía libre total.
        return (web) -> web.ignoring().requestMatchers("/uploads/covers/**");
    }

}
