package com.josec.catalog.repository;

import com.josec.catalog.model.MediaLogLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para los likes de los logs
 */
@Repository
public interface MediaLogLikeRepository extends JpaRepository<MediaLogLike, Long> {

    // Buscar like específico
    Optional<MediaLogLike> findByUserIdAndMediaLogId(Integer userId,  Integer mediaLogId);

    //Para saber rápido si un usuario le dio like (util en el feed)
    boolean existsByUserIdAndMediaLogId(Integer userId,  Integer mediaLogId);

    //Contar el total de likes de un post
    int countByMediaLogId(Integer mediaLogId);
}
