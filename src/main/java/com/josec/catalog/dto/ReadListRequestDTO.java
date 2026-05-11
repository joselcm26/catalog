package com.josec.catalog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReadListRequestDTO {

    @NotNull(message = "Book Id cannot be null")
    private Integer bookId;


}
