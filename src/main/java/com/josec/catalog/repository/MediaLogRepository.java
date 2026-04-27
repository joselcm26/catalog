package com.josec.catalog.repository;

import com.josec.catalog.model.MediaLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio universal de logs
 */
@Repository
public interface MediaLogRepository extends JpaRepository<MediaLog, Long> {

    /**
     * Feed Universal Paginado (Siguiendo + Propios no privados)
     *
     * @param myId Id del usuario
     * @param pageable datos de paginación
     * @return objeto página con logs de usuarios seguidos y los propios del usuario que sean públicos
     */
    @Query("SELECT m FROM MediaLog m WHERE " +
            "m.visibility != com.josec.catalog.model.enums.Visibility.PRIVATE AND " +
            "(m.user.id = :myId OR EXISTS (" +
            "    SELECT fc FROM FollowConnection fc WHERE fc.follower.id = :myId " +
            "    AND fc.followed.id = m.user.id AND fc.status = com.josec.catalog.model.enums.FollowStatus.ACCEPTED" +
            "))")
    Page<MediaLog> findUniversalFeed(@Param("myId") Integer myId, Pageable pageable);

}
