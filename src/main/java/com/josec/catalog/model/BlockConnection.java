package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad para representar un bloqueo de un usuario a otro, tabla intermedia
 */
@Getter
@Setter
@Entity
@Table(name = "block_connection", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"}) // Evita bloquear 2 veces a la misma persona
})
public class BlockConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El usuario que hace el bloqueo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker;

    // El usuario que hace queda bloqueado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
