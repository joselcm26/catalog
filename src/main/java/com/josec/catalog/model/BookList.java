package com.josec.catalog.model;

import com.josec.catalog.security.Ownable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementa las listas que el usuario podrá crear para guardar libros
 */
@Getter
@Setter
@Entity
@Table(name = "book_lists")
// 1. Convierte repository.delete() en un UPDATE automático
@SQLDelete(sql = "UPDATE book_lists SET deleted_at = NOW() WHERE id = ?")
// 2. Filtra automáticamente los borrados en todos los select
@SQLRestriction("deleted_at IS NULL")
public class BookList extends SoftDeleteable implements Ownable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id ;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private boolean isPublic = true;

    // --- RELACIONES ---

    // Relación del dueño (Un usuario puede crear muchas listas)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    //Relación para los colaboradores (Muchos a Muchos)
    @ManyToMany
    @JoinTable(
            name = "book_list_collaborators",
            joinColumns = @JoinColumn(name = "book_list_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> collaborators = new ArrayList<>();

    //Relación para los libros de la lista (Muchos a Muchos)
    @ManyToMany
    @JoinTable(
            name = "book_list_books",
            joinColumns = @JoinColumn(name = "book_list_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books = new ArrayList<>();
}
