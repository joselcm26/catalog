package com.josec.catalog.repository;

import com.josec.catalog.model.ReadingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para el log de lecturas del usuario
 */
@Repository
public interface ReadingLogRepository extends JpaRepository<ReadingLog, Integer> {

    /**
     * Obtiene la lista de logs del usuario con el Id dado
     * @param userId propietario
     * @return lista con los readlogs
     */
    List<ReadingLog> findByUserIdOrderByReadDateDesc(Integer userId);
}
