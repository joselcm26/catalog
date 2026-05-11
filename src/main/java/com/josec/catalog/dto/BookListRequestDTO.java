package com.josec.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookListRequestDTO {

    @NotBlank(message = "The list name cannot be blank")
    public String name;

    //Opcional
    public String description;

    public boolean isPublic;

}
