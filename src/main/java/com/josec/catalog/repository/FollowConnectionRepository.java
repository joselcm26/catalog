package com.josec.catalog.repository;

import com.josec.catalog.model.FollowConnection;
import com.josec.catalog.model.enums.FollowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowConnectionRepository extends JpaRepository<FollowConnection, Integer> {

    // 1. Buscar si existe la relación
    boolean existsByFollowerIdAndFollowedId(Integer followerId, Integer followedId);

    // 2. Buscar una conexión concreta
    Optional<FollowConnection> findByFollowerIdAndFollowedId(Integer followerId, Integer followedId);

    /**
     * Ver MIS seguidores
     *
     * @param followedId El usuario que lo solicita
     * @param status estado de las solicitudes (ACEPTADO o PENDIENTE)
     * @return Lista de conexiones de seguimiento
     */
    List<FollowConnection> findByFollowedIdAndStatus(Integer followedId, FollowStatus status);

    /**
     * Ver a quién SIGO
     *
     * @param followerId seguidor que mira a quién sigue
     * @param status estado de las solicitudes (ACEPTADO o PENDIENTE)
     * @return Lista de usuarios seguidos por el usuario
     */
    List<FollowConnection> findByFollowerIdAndStatus(Integer followerId, FollowStatus status);

    /**
     * Para utilizar en el bloqueo de usuarios
     *
     * @param followerId el que bloquea
     * @param followedId el bloqueado
     */
    @Transactional
    void deleteByFollowerIdAndFollowedId(Integer followerId, Integer followedId);

}
