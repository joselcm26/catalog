package com.josec.catalog.repository;

import com.josec.catalog.model.Book;
import com.josec.catalog.model.BookList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de listas de libros
 */
@Repository
public interface BookListRepository extends JpaRepository<BookList, Integer> {

    List<BookList> findByOwnerId(int id);

}
