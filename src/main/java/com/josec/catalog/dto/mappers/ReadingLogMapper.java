package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.ReadingLogRequestDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.model.ReadingLog;
import org.mapstruct.Mapper;

// Con uses le decimos que utilice ese mapper si lo necesita
@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface ReadingLogMapper {

    ReadingLogResponseDTO toDTO(ReadingLog readingLog);

    ReadingLog toEntity(ReadingLogRequestDTO readingLogRequestDTO);
}
