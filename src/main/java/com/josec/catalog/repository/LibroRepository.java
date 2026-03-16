package com.josec.catalog.repository;

import com.josec.catalog.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// REPOSITORIO - CONEXIÓN DE DATOS

// Al heredar de JpaRepository, Spring Boot te regala
// métodos como save(), findAll(), findById(), deleteById()...
// No hay que escribir ni una sola línea de SQL.
@Repository
public interface LibroRepository extends JpaRepository<Libro, Integer>{

}
