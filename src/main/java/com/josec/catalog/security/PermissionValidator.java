package com.josec.catalog.security;

import com.josec.catalog.exception.AccessDeniedException;
import com.josec.catalog.exception.UserNotFoundException;
import com.josec.catalog.model.User;
import com.josec.catalog.model.enums.Visibility;
import com.josec.catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Comprobador de permisos de acceso de usuario
 */
@Component
public class PermissionValidator {


    public void checkPermissions(Ownable entity) {
        // 1. Si no es privado, tódo el mundo puede verlo (sale rápido)
        if (entity.getVisibility().equals(Visibility.PUBLIC)) {
            return;
        }

        // 2. Si es privado, comprobamos quién está logueado
        // (Asumiendo que guardamos el ID en los detalles como hicimos en JwtFilter)
        Integer loggedInUserId = null;
        Object details = Objects
                .requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getDetails();
        if (details instanceof Integer) {
            loggedInUserId = (Integer) details;
        }

        // 3. Comprobamos la propiedad
        if (loggedInUserId == null || !entity.getOwner().getId().equals(loggedInUserId)) {
            throw new AccessDeniedException("Access denied. You are not allowed to perform this action.");
        }
    }

    public void checkPermissionByOwnerId(Integer requestOwnerId) {
        Integer userId = (Integer) Objects
                .requireNonNull(Objects.
                        requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getDetails());
        // 2. Si es privado, comprobamos quién está logueado
        // (Asumiendo que guardamos el ID en los detalles como hicimos en JwtFilter)
        Integer loggedInUserId = null;
        Object details = Objects
                .requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getDetails();
        if (details instanceof Integer) {
            loggedInUserId = (Integer) details;
        }

        // 3. Comprobamos la propiedad
        if (!requestOwnerId.equals(loggedInUserId)) {
            throw new AccessDeniedException("Access denied. You are not allowed to perform this action.");
        }
    }

    /**
     * Determina quien está loggeado
     *
     * @return Id del usuario
     */
    public Integer whoIsLoggedIn() {
        Integer loggedInUserId = null;
        Object details = Objects
                .requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getDetails();
        if (details instanceof Integer) {
            loggedInUserId = (Integer) details;
        }
        if (loggedInUserId == null ) {
            throw new UserNotFoundException("User not found or not logged in.");
        }else{
            return loggedInUserId;
        }
    }
}
