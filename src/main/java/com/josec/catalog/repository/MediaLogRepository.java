package com.josec.catalog.repository;

import com.josec.catalog.model.MediaLog;
import com.josec.catalog.model.ReadingLog;
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

    // TODO: CAMBIAR A SLICE PARA SCROLL INFINITO (?)
    /**
     * Query para el log del usuario.
     * Muestra todas sus entradas.
     *
     * @param ownerId Id del propietario
     * @return Lista de readingLogs
     */
    // MI DIARIO (Personal)
    // El dueño ve absolutamente todos sus registros.
    @Query("SELECT m FROM MediaLog m WHERE m.user.id = :ownerId")
    Page<MediaLog> findMyDiary(@Param("ownerId") Integer ownerId, Pageable pageable);

    /**
     * Query para el feed social.
     * Se muestran los post de visibilidad para amigos, sin mostrar los contactos bloqueados.
     *
     * @param myId Id del que hace la consulta
     * @return Lista de mediaLogs
     */
    @Query("SELECT m FROM MediaLog m WHERE " +
            "m.visibility != com.josec.catalog.model.enums.Visibility.PRIVATE AND " +
            "(m.user.id = :myId OR EXISTS (" +
            "    SELECT fc FROM FollowConnection fc WHERE fc.follower.id = :myId " +
            "    AND fc.followed.id = m.user.id AND fc.status = com.josec.catalog.model.enums.FollowStatus.ACCEPTED" +
            ")) AND " +
            "NOT EXISTS (" +
            "    SELECT bc FROM BlockConnection bc WHERE " +
            "    (bc.blocker.id = :myId AND bc.blocked.id = m.user.id) OR " +
            "    (bc.blocker.id = m.user.id AND bc.blocked.id = :myId)" +
            ")")
    Page<MediaLog> findHomeFeed(@Param("myId") Integer myId, Pageable pageable);

    /**
     * Query para el feed de explorar
     * Se muestran publicaciones de todos
     *
     * @return Lista de mediaLogs
     */
    // EXPLORAR (Global / Descubrimiento)
    // Regla: Solo cosas marcadas como PÚBLICAS.
    @Query("SELECT m FROM MediaLog m WHERE " +
            "m.visibility = com.josec.catalog.model.enums.Visibility.PUBLIC AND " +
            "NOT EXISTS (" +
            "    SELECT bc FROM BlockConnection bc WHERE " +
            "    (bc.blocker.id = :myId AND bc.blocked.id = m.user.id) OR " +
            "    (bc.blocker.id = m.user.id AND bc.blocked.id = :myId)" +
            ")")
    Page<MediaLog> findExploreFeed(@Param("myId") Integer myId, Pageable pageable);

}
