package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    private String comment;

    // RELACIONES DE TABLAS

    // Relación con Book
    @ManyToOne(fetch = FetchType.LAZY) //"Muchas" reseñas pertenecen a "Un" libro
    @JoinColumn(name = "book_id") // Así se llamará la columna en la base de datos
    private Book book;

    //Relación con users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
