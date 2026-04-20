package com.josec.catalog.repository;

import com.josec.catalog.model.Book;
import com.josec.catalog.model.BookList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de listas de libros
 */
@Repository
public interface BookListRepository extends JpaRepository<BookList, Integer> {

    List<BookList> findByOwnerId(int id);

    // 1. Buscar un registro que está EN la papelera
    // Usamos nativeQuery = true para ignorar el @SQLRestriction
    @Query(value= "SELECT * FROM book_lists  WHERE id = :bookListId AND deleted_at IS NOT NULL", nativeQuery = true)
    BookList findDeletedById(@Param("bookListId") Integer bookListId);

    // 2. Borrado físico definitivo (Limpieza de la escoba)
    // @Modifying es obligatorio para consultas que alteran datos (DELETE/UPDATE)
    @Modifying
    @Query(value = "DELETE FROM book_lists WHERE deleted_at <= :threshold", nativeQuery = true)
    void deletePermanentlyOlderThan(@Param("threshold") LocalDateTime threshold);

    // 3. Ver toda la papelera del usuario
    @Query(value = "SELECT * FROM book_lists WHERE owner_id = :ownerId AND deleted_at IS NOT NULL", nativeQuery = true)
    List<BookList> findAllDeletedByUserId(@Param("ownerId") Integer ownerId);

}
