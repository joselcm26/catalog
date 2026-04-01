package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
//@Entity
//@Table(name = "movies")
/*
 * Clase entidad para las películas.
 *
 * Clave primaria: ID autogenerando
 * Atributos: String title, String synopsis, String director, Integer year, List Reviews (OneToMany)
 */
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;
    private String synopsis;
    private String director;
    private Integer year;

    //TODO: overall rating - media de las valoraciones colocadas por los usuarios

    // RELACIONES CON TABLAS

    // Relación con Review
//    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Review> reviews= new ArrayList<>();
}
