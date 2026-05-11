package com.josec.catalog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccessLogResponseDTO {

    private Integer userId;
    private String username;
    private LocalDateTime loggedAt;
    private String ipAddress;
    private String userAgent;
    private boolean isSuccess;


}
