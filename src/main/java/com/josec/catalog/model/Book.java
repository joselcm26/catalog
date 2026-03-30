package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Data; //Crea los getters y setters de forma invisible. Están, pero no se ven.
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// EL MODELO - LA ENTIDAD LIBRO

@Getter
@Setter
@Entity //Le dice a Spring: "Esta clase es una tabla en la base de datos"
@Table(name="books")
public class Book {

    @Id // Esta es la clave primaria (Primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Se autorincrementa
    private Long id;

    private String title;
    private String author;
    private Integer publicationYear;
    private String synopsis;

    //TODO: overall rating - media de las valoraciones colocadas por los usuarios

    // RELACIONES CON TABLAS

    // Relación con Review
    // mappedBy = "book": Le dice a Spring que la configuración real está en la variable "book" de la clase Review.
    // Cascade = CascadeType.ALL: Si borras un libro, ¡se borran todas sus reseñas automáticamente! (Lógico, ¿no?)
    // orphanRemoval = true: Si desconectas una reseña de un libro, se borra de la BD.
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews= new ArrayList<>();

}
