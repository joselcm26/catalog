package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity //Le dice a Spring: "Esta clase es una tabla en la base de datos"
@Table(name="Libros")
public class Libro {

    @Id // Esta es la clave primaria (Primary key)
    private Long id;

    private String titulo;
    private String autor;
    private Integer anioPublicacion;
    private String sinopsis;
    
}
