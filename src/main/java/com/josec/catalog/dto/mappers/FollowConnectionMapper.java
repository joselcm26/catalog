package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.FollowConnectionResponseDTO;
import com.josec.catalog.model.FollowConnection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowConnectionMapper {

    @Mapping(source = "followed.id", target = "followed")
    @Mapping(source = "follower.id", target = "follower")
    FollowConnectionResponseDTO toDTO(FollowConnection followConnection);
}
