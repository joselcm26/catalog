package com.josec.catalog.repository;

import com.josec.catalog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring leerá el nombre de este métödo y sabrá automáticamente
    // que debe hacer un "SELECT * FROM users WHERE email = ?"
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    /**
     * Consulta JPQL para buscar usuarios en la base de datos y que además, filtre por
     * usuarios bloqueados.
     *
     * @param query texto de la búsqueda
     * @param myId Id de usuario del que busca
     * @param pageable parámetros de página
     * @return página con los resultados
     */
    @Query(value = "SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) AND " +
            "u.id != :myId AND " +
            "u.role != 'ADMIN' AND " + // No mostramos las cuentas de administrador
            "NOT EXISTS (" +           // Ocultamos las cuentas bloqueadas
            "    SELECT bc FROM BlockConnection bc WHERE " +
            "    (bc.blocker.id = :myId AND bc.blocked.id = u.id) OR " +
            "    (bc.blocker.id = u.id AND bc.blocked.id = :myId)" +
            ")")
    Page<User> searchUsers(@Param("query") String query, @Param("myId") Integer myId, Pageable pageable);

}
