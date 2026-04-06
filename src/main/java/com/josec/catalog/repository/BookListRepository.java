package com.josec.catalog.repository;

import com.josec.catalog.model.BookList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de listas de libros
 */
@Repository
public interface BookListRepository extends JpaRepository<BookList, Integer> {
}
