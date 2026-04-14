package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa la lista de lectura de libros pendientes del usuario
 */
@Getter
@Setter
@Entity
@Table(name = "readlists")
public class ReadList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Relación Uno a Uno (una lista de lectura es de un solo usuario)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    //Relación para los libros de la lista (Muchos a Muchos) - Tabla intermedia
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "read_list_books",
            joinColumns = @JoinColumn(name = "read_list_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books = new ArrayList<>();

    // --- Métodos Utilitarios ---

    public void addBook(Book book) {
        // Evitamos añadir el mismo libro dos veces a la lista
        if (!this.books.contains(book)) {
            this.books.add(book);
        }
    }

    public void removeBook(Book book) {
        this.books.remove(book);
    }
}
