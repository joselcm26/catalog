package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.AccessLogResponseDTO;
import com.josec.catalog.model.AccessLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccessLogMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    AccessLogResponseDTO toDTO(AccessLog accessLog);
}
