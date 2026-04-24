package com.josec.catalog.repository;

import com.josec.catalog.model.FollowConnection;
import com.josec.catalog.model.enums.FollowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowConnectionRepository extends JpaRepository<FollowConnection, Integer> {

    // 1. Buscar si existe la relación
    boolean existsByFollowerIdAndFollowedId(Integer followerId, Integer followedId);

    // 2. Buscar una conexión concreta
    Optional<FollowConnection> findByFollowerIdAndFollowedId(Integer followerId, Integer followedId);

    // 3. Ver MIS seguidores (Aceptados)
    List<FollowConnection> findByFollowedIdAndStatus(Integer followedId, FollowStatus status);

    // 4. Ver a quién SIGO (Aceptados)
    List<FollowConnection> findByFollowerIdAndStatus(Integer followerId, FollowStatus status);

}
