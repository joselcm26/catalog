package com.josec.catalog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReadListRequestDTO {

    @NotNull(message = "ReadList Id cannot be null")
    private Integer id;

    @NotNull(message = "OwnerId cannot be null")
    private Integer ownerId;


}
