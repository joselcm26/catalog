package com.josec.catalog.dto;

import lombok.Data;

/**
 * DTO para la búsqueda de usuarios
 */
@Data
public class UserSummaryResponseDTO {

    private Integer userId;
    private String username;
    private String profileImage;
    private boolean isPrivateProfile; // Util para pintar un candado en el front
}
