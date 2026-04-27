package com.josec.catalog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase dedicada a los likes de un ReadingLog
 */
@Getter
@Setter
@Entity
@Table(name = "media_log_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "media_log_id"})
})
public class MediaLogLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_log_id", nullable = false)
    private MediaLog mediaLog;
}
