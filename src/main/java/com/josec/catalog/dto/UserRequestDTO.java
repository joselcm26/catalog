package com.josec.catalog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class UserRequestDTO {

    @NotBlank(message = "User name cannot be blank")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Must be a well-formed email address")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

}
