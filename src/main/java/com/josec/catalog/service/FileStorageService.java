package com.josec.catalog.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
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

    public enum ImageType {
        COVER,
        PROFILE

    }

    //Leer ruta desde application.properties. Si no existe, por defecto será "uploads/covers"

    @Value("${file.upload-dir:uploads/covers}")
    private String uploadDir;

    @Value("${file.upload.profile-dir:uploads/profile}")
    private String profileUploadDir;

    private Path coverStorageLocation;
    private Path profileStorageLocation;

    /**
     * @PostConstruct hace que se ejectute nada más arrangar Spring
     */
    @PostConstruct
    public void init() {
        //Convertir el texto "uploads/**" a una ruta real del sistema
        this.coverStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.profileStorageLocation = Paths.get(profileUploadDir).toAbsolutePath().normalize();
        try {
            //Si la carpeta no existe, se crea
            Files.createDirectories(this.coverStorageLocation);
            Files.createDirectories(this.profileStorageLocation);
        }catch (Exception e){
            throw new RuntimeException("Could not create directories storage services");
        }
    }

    /**
     * Guarda el cover y devuelve el nombre único generado
     */
    public String saveImage(MultipartFile file, ImageType imageType) {
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
            Path targetLocation = null;
            if(imageType == ImageType.COVER){
                targetLocation = this.coverStorageLocation.resolve(newFileName);
            }else {
                targetLocation = this.profileStorageLocation.resolve(newFileName);
            }
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
