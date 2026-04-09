package com.josec.catalog.repository;

import com.josec.catalog.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// REPOSITORIO - CONEXIÓN DE DATOS

// Al heredar de JpaRepository, Spring Boot te regala
// métodos como save(), findAll(), findById(), deleteById()...
// No hay que escribir ni una sola línea de SQL.
@Repository
public interface BookRepository extends JpaRepository<Book, Integer>{

    /**
     * Buscar libros cuyo título contenga la palabra clave, sin distinguir mayúsculas o minúsculas
     *
     * @param title clave de la búsqueda
     * @return lista de libros si se encuentran resultados
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Búsqueda cruzada usando JPQL
    @Query("SELECT b FROM BookList bl JOIN bl.books b " +
            "WHERE bl.id = :listId AND LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> searchBooksInsideList(@Param("listId") int listId, @Param("query") String query);

}
