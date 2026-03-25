package com.josec.catalog.service;


import com.josec.catalog.exception.BookNotFoundException;
import com.josec.catalog.model.Book;
import com.josec.catalog.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Le indicamos a Spring que esta es nuestra capa de lógica de negocio
public class BookService {

    @Autowired
    private BookRepository bookRepository; // El servicio sí habla con la base de datos

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book createBook(Book libro) {
       if (libro != null) bookRepository.save(libro);
       return libro;
    }

    // AQUÍ ESTÁ LA MAGIA DEL MANEJO DE ERRORES
    public Book getBookById(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(("No se encontró el libro con el ID: " + id)));
    }

    public Book updateBook(int id, Book updatedBook) {

        Book existingBook = getBookById(id);

        // Si no saltó la excepción, actualizamos los datos
        existingBook.setTitulo(updatedBook.getTitulo());
        existingBook.setAutor(updatedBook.getAutor());
        existingBook.setAnioPublicacion(updatedBook.getAnioPublicacion());
        existingBook.setSinopsis(updatedBook.getSinopsis());

        return bookRepository.save(existingBook);

    }

    public void deleteBook(int id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }
}
