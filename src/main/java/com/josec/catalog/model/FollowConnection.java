package com.josec.catalog.model;

import com.josec.catalog.model.enums.FollowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Tabla intermedia para los seguimientos de usuarios.
 */
@Getter
@Setter
@Entity
@Table(name ="follow_connection")
public class FollowConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Usuario que hace clic en seguir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    //Usuario que recibe el seguimiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    // Estado de la solicitud (si el usuario followed tiene el perfil privado, permanecerá como PENDING hasta
    // ser acceptada
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowStatus status;

    //Fecha de creación del seguimiento
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt =  LocalDateTime.now();
}
