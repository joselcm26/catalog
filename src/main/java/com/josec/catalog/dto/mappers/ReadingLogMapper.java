package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.ReadingLogRequestDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.model.ReadingLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

// Con uses le decimos que utilice ese mapper si lo necesita
@Mapper(componentModel = "spring", uses = {BookMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReadingLogMapper {

    @Mapping(source = "user.username", target = "username") // Navega del padre al usuario
    @Mapping(source = "id", target = "id") // El ID viene de MediaLog
    ReadingLogResponseDTO toDTO(ReadingLog readingLog);

    ReadingLog toEntity(ReadingLogRequestDTO readingLogRequestDTO);
}
