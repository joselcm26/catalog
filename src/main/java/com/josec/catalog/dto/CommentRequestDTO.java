package com.josec.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestDTO {
    @NotBlank(message = "The comment cannot be blank")
    @Size(max=500, message = "The comment cannot be larger than 500 characters")
    String content;
}
