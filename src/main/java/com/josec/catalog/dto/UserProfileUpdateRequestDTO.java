package com.josec.catalog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Singular;

import java.time.LocalDate;

/**
 * Se usa para añadir o actualizar datos opcionales del usuario.
 */
@Data
public class UserProfileUpdateRequestDTO {

    @Size(max = 500, message = "Biography cannot be larger than 500 characters")
    private String bio;

    @Past(message = "Birth date must be a date in the past")
    private LocalDate birthDate;

    // Acepta números con o sin el "+" delante, entre 7 y 15 dígitos
    @Pattern(regexp = "\\+?[0-9]{7,15}$", message = "Phone number format invalid")
    private String phone;

    @Size(max = 255, message = "Address cannot be larger than 100 characters")
    private String address;

    @Size(max = 100, message = "City cannot be larger than 100 characters")
    private String city;

    @Size(max = 100, message = "Province cannot be larger than 100 characters")
    private String province;

    @Size(max = 100, message = "Country cannot be larger than 100 characters")
    private String country;

    private boolean isPrivateProfile;

}
