package com.josec.catalog.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * Servicio para leer y escribir archivos de imagen para las carátulas
 */
@Service
public class FileStorageService {

    //Leer ruta desde application.properties. Si no existe, por defecto será "uploads/covers"

    @Value("${file.upload-dir:uploads/covers}")
    private String uploadDir;

    private Path fileStorageLocation;

    /**
     * @PostConstruct hace que se ejectute nada más arrangar Spring
     */
    @PostConstruct
    public void init() {
        //Convertir el texto "uploads/covers" a una ruta real del sistema
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            //Si la carpeta no existe, se crea
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception e){
            throw new RuntimeException("Could not create directory storage service");
        }
    }

    /**
     * Guarda la imagen y devuelve el nombre único generado
     */
    public String saveCoverImage(MultipartFile file) {
        // 1. Validaciones de seguridad
        // Archivo que no sea vacío
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file " + file.getOriginalFilename());
        }

        //Que sea del tipo imagen
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed - " + file.getOriginalFilename());
        }

        // 2. Extraer la extensión original
        // Nombre
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = "";
        if (originalFilename.contains(".")) { // Extraer lo que haya después del punto
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 3. Generar el nombre único para el archivo (para evitar nombres iguales)
        // UUID - Clase para crear identificadores únicos
        String newFileName = UUID.randomUUID().toString() + fileExtension;
        try {
            // 4. Montar la ruta final: "C:/.../uploads/covers/name.jpg"
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);

            // 5. Copiar el archivo que nos llega al disco duro
            // StandardCopyOption.REPLACE_EXISTING sobreescribe si por algún milagro el UUID se repitiera
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 6. Devolvemos solo el nombre del archivo que es lo que se guarda en la base de datos
            return newFileName;

        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + newFileName + ". Please try again!", e);
        }
    }
}
