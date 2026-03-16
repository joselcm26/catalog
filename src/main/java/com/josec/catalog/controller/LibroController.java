package com.josec.catalog.controller;

import com.josec.catalog.model.Libro;
import com.josec.catalog.controller.LibroController;

import com.josec.catalog.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros") //Todas las rutas empezarán por aquí
public class LibroController {

    @Autowired
    private LibroRepository libroRepository; //Inyección de repositorio para poder usarlo

    @GetMapping // GET HTTP
    public List<Libro> findAll() {
        return libroRepository.findAll(); //Busca en la BD y lo devuelve
    }

    @PostMapping // POST HTTP
    public Libro save(@RequestBody Libro libro) {
        return libroRepository.save(libro); //Guarda el libro que nos llega en la BD
    }

}
