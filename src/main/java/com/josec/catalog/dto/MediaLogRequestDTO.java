package com.josec.catalog.dto;

import com.josec.catalog.model.enums.Visibility;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Clase abstracta DTO padre para los logs
 */
@Data
public abstract class MediaLogRequestDTO {

    @NotNull(message = "You must to specify the visibility of the media log")
    private Visibility visibility;
}
