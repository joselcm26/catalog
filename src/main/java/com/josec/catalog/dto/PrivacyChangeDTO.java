package com.josec.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrivacyChangeDTO {

    @NotNull(message = "Privacy param cannot be blank")
    private boolean isPrivateProfile;
}
