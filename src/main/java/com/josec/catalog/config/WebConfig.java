package com.josec.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(uploadDir);
        String absolutePath = path.toFile().getAbsolutePath();

        // Si la petición empieza por /uploads/**, busca en la carpeta física
        registry.addResourceHandler("/uploads/covers/**")
                .addResourceLocations("file:" + absolutePath + "/");

    }
}
