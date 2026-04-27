package com.josec.catalog.security;

import com.josec.catalog.model.User;
import com.josec.catalog.model.enums.Visibility;

/**
 * Interfaz que extenderán las entidades que implementen listas, con los métodos básicos
 * getOwner e isPublic para comprobaciones de seguridad.
 *
 */
public interface Ownable {
    User getOwner();
    Visibility getVisibility();
}
