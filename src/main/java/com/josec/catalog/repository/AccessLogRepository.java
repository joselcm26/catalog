package com.josec.catalog.repository;

import com.josec.catalog.model.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de registros de acceso
 */
@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    /**
     * Obtiene los registros de acceso ordenados por fecha descendiente
     * @param id del usuario
     * @return lista de objetos AccessLog del usuario
     */
    Page<AccessLog> findByUserIdOrderByLoggedAtDesc(Integer id, Pageable pageable);

}
