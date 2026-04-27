package com.josec.catalog.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;


/**
 * Representa el log de lectura del usuario
 */
@Getter
@Setter
@Entity
@Table(name = "reading_logs")
@PrimaryKeyJoinColumn(name = "media_log_id") // Une esta tabla con la de MediaLog
public class ReadingLog extends MediaLog {

    //Relación con el libro
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    //Fecha de registro, elegible por el usuario
    @Column(name = "read_date")
    private LocalDate readDate;

    //Puntiación opcional del 1 al 5
    @Column(name = "rating")
    private Integer rating;

    //Comentario personal privado opcional
    @Column(name = "private_comment", columnDefinition = "TEXT")
    private String privateComment;


}
