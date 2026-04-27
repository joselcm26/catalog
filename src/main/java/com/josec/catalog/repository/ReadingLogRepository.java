package com.josec.catalog.repository;

import com.josec.catalog.model.ReadingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para el log de lecturas del usuario
 */
@Repository
public interface ReadingLogRepository extends JpaRepository<ReadingLog, Integer> {
    

    // 1. Buscar un registro que está EN la papelera
    // Usamos nativeQuery = true para ignorar el @SQLRestriction
    @Query(value= "SELECT * FROM reading_logs  WHERE id = :logId AND deleted_at IS NOT NULL", nativeQuery = true)
    ReadingLog findDeletedById(@Param("logId") Integer logId);

    // 2. Borrado físico definitivo (Limpieza de la escoba)
    // @Modifying es obligatorio para consultas que alteran datos (DELETE/UPDATE)
    @Modifying
    @Query(value = "DELETE FROM reading_logs WHERE deleted_at <= :threshold", nativeQuery = true)
    void deletePermanentlyOlderThan(@Param("threshold") LocalDateTime threshold);

    // 3. Ver toda la papelera del usuario
    @Query(value = "SELECT * FROM reading_logs WHERE owner_id = :ownerId AND deleted_at IS NOT NULL", nativeQuery = true)
    List<ReadingLog> findAllDeletedByUserId(@Param("ownerId") Integer ownerId);

    /**
     * Query para el log del usuario.
     * Muestra todas sus entradas.
     * 
     * @param ownerId Id del propietario
     * @return Lista de readingLogs
     */
    // MI DIARIO (Personal)
    // El dueño ve absolutamente todos sus registros.
    @Query("SELECT rl FROM ReadingLog rl WHERE rl.user.id = :ownerId ORDER BY rl.readDate DESC")
    List<ReadingLog> findMyDiary(@Param("ownerId") Integer ownerId);
    
    /**
     * Query para el feed social.
     * Se muestran los post de visibilidad para amigos.
     *
     * @param ownerId Id del que hace la consulta
     * @return Lista de readingLogs
     */
    // Regla: No es privado AND (es mío OR sigo al autor)
    @Query("SELECT rl FROM ReadingLog rl WHERE " +
            "rl.visibility != com.josec.catalog.model.enums.Visibility.PRIVATE AND " +
            "(rl.user.id = :ownerId OR EXISTS (" +
            "    SELECT fc FROM FollowConnection fc WHERE fc.follower.id = :ownerId " +
            "    AND fc.followed.id = rl.user.id AND fc.status = com.josec.catalog.model.enums.FollowStatus.ACCEPTED" +
            ")) " +
            "ORDER BY rl.createdAt DESC")
    List<ReadingLog> findHomeFeed(@Param("ownerId") Integer ownerId);

    /**
     * Query para el feed de explorar
     * Se muestran publicaciones de todos
     * 
     * @return Lista de readingLogs
     */
    // EXPLORAR (Global / Descubrimiento)
    // Regla: Solo cosas marcadas como PÚBLICAS.
    @Query("SELECT rl FROM ReadingLog rl WHERE " +
            "rl.visibility = com.josec.catalog.model.enums.Visibility.PUBLIC " +
            "ORDER BY rl.createdAt DESC")
    List<ReadingLog> findExploreFeed();

}
