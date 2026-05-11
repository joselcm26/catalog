package com.josec.catalog.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.josec.catalog.model.enums.Visibility;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
//Añadir un campo al JSON llamado mediaType
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "mediaType"
)
//Le decimos qué valor debe poner en mediaType
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReadingLogResponseDTO.class, name = "BOOK")
        //TODO: añadir aquí los demás tipos de logs cuando estén hechos
})
public class MediaLogResponseDTO {

    private Integer id;
    private String username;
    private Visibility visibility;
    private LocalDateTime createdAt;

    // Datos de interacción
    private Integer likeCount;
    private Boolean iLikedIt;

    //Comentarios
    private List<CommentResponseDTO> comments;
}
