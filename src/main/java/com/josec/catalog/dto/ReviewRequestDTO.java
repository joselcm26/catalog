package com.josec.catalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequestDTO {

    @Min(value = 1, message = "The minimun rating is 1")
    @Max(value = 5, message = "The maximun rating is 5")
    private int rating;

    @NotBlank(message = "The comment cannot be empty")
    private String comment;
}
