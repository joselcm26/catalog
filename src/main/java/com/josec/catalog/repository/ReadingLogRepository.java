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

    /**
     * Obtiene la lista de logs del usuario con el Id dado
     *
     * @param ownerID propietario
     * @return lista con los readlogs
     */
    List<ReadingLog> findByOwnerIdOrderByReadDateDesc(Integer ownerID);

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
}
