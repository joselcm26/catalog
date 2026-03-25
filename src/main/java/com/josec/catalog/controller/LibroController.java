package com.josec.catalog.controller;

import com.josec.catalog.model.Libro;
import com.josec.catalog.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        if(libroRepository.findById(id).isPresent()) {
            Libro libro = libroRepository.findById(id).get();
            return ResponseEntity.ok(libro);
        }
        return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable int id) {
        if (libroRepository.findById(id).isPresent()) {
            Libro libro = libroRepository.findById(id).get();
            libroRepository.delete(libro);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping // POST HTTP
    public Libro save(@RequestBody Libro libro) {
        return libroRepository.save(libro); //Guarda el libro que nos llega en la BD
    }

    @PutMapping
    public ResponseEntity<?> putBook(@RequestBody Libro libro) {
        if (libroRepository.findById(libro.getId().intValue()).isPresent()){
            Libro lib = libroRepository.findById(libro.getId().intValue()).get();
            if(lib.getId() != 0){
                libroRepository.save(libro);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
