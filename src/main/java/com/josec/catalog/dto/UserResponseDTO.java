package com.josec.catalog.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String bio;
    private LocalDate birthDate;
    private String phone;
    private String address;
    private String city;
    private String province;
    private String country;
    private String profileImage;

}
