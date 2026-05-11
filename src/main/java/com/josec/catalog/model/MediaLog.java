package com.josec.catalog.model;

import com.josec.catalog.model.enums.Visibility;
import com.josec.catalog.security.Ownable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad base para todos los tipos de Logs.
 * Usaremos la estrategia JOINED, que crea una tabla para los datos comunes y tablas separadas
 * para los detalles específicos (como el libro o la película).
 */
@Getter
@Setter
@Entity
@Table(name = "media_logs")
@Inheritance(strategy = InheritanceType.JOINED) // 🚀 La clave de la arquitectura
// 1. Convierte repository.delete() en un UPDATE automático
@SQLDelete(sql = "UPDATE reading_logs SET deleted_at = NOW() WHERE id = ?")
// 2. Filtra automáticamente los borrados en todos los select
@SQLRestriction("deleted_at IS NULL")
public abstract class MediaLog extends SoftDeleteable implements Ownable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.PRIVATE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Las interacciones se anclan aquí para que sirvan para cualquier medio
    @OneToMany(mappedBy = "mediaLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "mediaLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MediaLogLike> likes = new ArrayList<>();

    @Override
    public User getOwner() {
        return user;
    }
}