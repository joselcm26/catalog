package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Games")
/*
 * Clase entidad para los juegos.
 *
 * Clave primaria: ID autogenerando
 * Atributos: String title, String synopsis, String studio, Integer year, List Reviews (OneToMany)
 */
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String synopsis;
    private String studio;
    private Integer year;

    // RELACIONES CON TABLAS

    // Relación con Review
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews= new ArrayList<>();


}
