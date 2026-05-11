package com.josec.catalog.repository;

import com.josec.catalog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Buscar comentarios de un MediaLog por Id, ordenado en descendiente por fecha
     *
     * @param mediaLogId del que se van a buscar
     * @param pageable paginación
     * @return página de logs
     */
    Page<Comment> findByMediaLogIdOrderByCreationDateDesc(Long mediaLogId, Pageable pageable);
}
