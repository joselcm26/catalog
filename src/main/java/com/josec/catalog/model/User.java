package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    // -- DATOS BÁSICOS --
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER; // Todô usuario nuevo por defecto es usuario normal

    // -- DATOS OPCIONALES (NO SE PIDEN EN EL REGISTRO) --

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "birth_date") // Usar siempre LocalDate, nunca Date
    private LocalDate birthDate;
    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String province;

    @Column(length = 100)
    private String country;

    @Column(name = "profile_image")
    private String profileImage;


    //-- RELACIONES CON TABLAS --

    // Un usuario puede escribir MUCHAS reseñas
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();





}
