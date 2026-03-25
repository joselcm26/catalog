package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Data; //Crea los getters y setters de forma invisible. Están, pero no se ven.

// EL MODELO - LA ENTIDAD LIBRO

@Data
@Entity //Le dice a Spring: "Esta clase es una tabla en la base de datos"
@Table(name="Libros")
public class Book {

    @Id // Esta es la clave primaria (Primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Se autorincrementa
    private Long id;

    private String titulo;
    private String autor;
    private Integer anioPublicacion;
    private String sinopsis;

}
