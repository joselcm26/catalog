package com.josec.catalog.dto;

import com.josec.catalog.model.User;
import com.josec.catalog.model.enums.FollowStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowConnectionResponseDTO {

    private Integer follower;
    private Integer followed;
    private FollowStatus status;
    private LocalDateTime createdAt;
}
