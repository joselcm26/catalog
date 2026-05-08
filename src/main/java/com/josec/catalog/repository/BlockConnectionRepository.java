package com.josec.catalog.repository;

import com.josec.catalog.dto.MediaLogResponseDTO;
import com.josec.catalog.model.BlockConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de bloqueos de contactos
 */
@Repository
public interface BlockConnectionRepository extends JpaRepository<BlockConnection, Long> {
    /**
     * Comprobar si existe un bloqueo
     * @param blockerId bloqueador
     * @param blockedId bloqueado
     * @return true si existe, false si no
     */
    boolean existByBlockerIdAndBlockerId(Long blockerId, Long blockedId);

    /**
     * Buscar bloqueo para utilizar en desbloqueo
     *
     * @param blockerId bloqueador
     * @param blockedId bloqueado
     * @return BlockConnection si existe
     */
    Optional<BlockConnection> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    /**
     *  Ultra-útil para el futuro: ¿Hay un bloqueo en CUALQUIER dirección?
     *  Útil para saber si A puede ver el perfil de B.
     * @param blockerId1 bloqueador
     * @param blockedId1 bloqueado
     * @param blockerId2 bloqueado
     * @param blockedId2 bloqueador
     * @return true si existe en alguna dirección, false si no existe en ninguna
     */
    boolean existsByBlockerIdAndBlockedIdOrBlockerIdAndBlockerId(
            Long blockerId1, Long blockedId1, Long blockerId2, Long blockedId2);
}
