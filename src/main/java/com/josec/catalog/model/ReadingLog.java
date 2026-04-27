package com.josec.catalog.model;

import com.josec.catalog.model.enums.Visibility;
import com.josec.catalog.security.Ownable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa el log de lectura del usuario
 */
@Getter
@Setter
@Entity
@Table(name = "reading_logs")
// 1. Convierte repository.delete() en un UPDATE automático
@SQLDelete(sql = "UPDATE reading_logs SET deleted_at = NOW() WHERE id = ?")
// 2. Filtra automáticamente los borrados en todos los select
@SQLRestriction("deleted_at IS NULL")
public class ReadingLog extends SoftDeleteable implements Ownable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Relación con el usuario (Dueño)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    //Relación con el libro
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    //Fecha de registro, elegible por el usuario
    @Column(name = "read_date", nullable = false)
    private LocalDate readDate;

    //Puntiación opcional del 1 al 5
    @Column(name = "rating")
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.PRIVATE; // Privado por defecto

    //Comentario personal privado opcional
    @Column(name = "private_comment", columnDefinition = "TEXT")
    private String privateComment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt =  LocalDateTime.now();


}
