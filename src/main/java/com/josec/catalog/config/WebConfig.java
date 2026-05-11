package com.josec.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase para mapear la ruta física a una la ruta web
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/covers}")
    private String uploadDir;

    @Value("${file.upload.profile-dir:uploads/profile}")
    private String profileUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Paths
        Path path = Paths.get(uploadDir);
        Path profilePath = Paths.get(profileUploadDir);

        // 2. Absolute paths
        String absolutePath = path.toFile().getAbsolutePath();
        String profileAbsolutePath = profilePath.toFile().getAbsolutePath();

        // Si la petición empieza por /uploads/**, busca en la carpeta física
        registry.addResourceHandler("/uploads/covers/**")
                .addResourceLocations("file:" + absolutePath + "/");

        // Si la petición empieza por /uploads/**, busca en la carpeta física
        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations("file:" + profileAbsolutePath + "/");

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplicar a todas nuestras rutas de la API
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") // Las URLs de Front-end (ej: React, Vite)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permitir cualquier cabecera (incluyendo nuestro Authorization Bearer)
                .allowCredentials(true); // Necesario si en el futuro usas cookies, o para ciertos flujos de Auth
    }
}
