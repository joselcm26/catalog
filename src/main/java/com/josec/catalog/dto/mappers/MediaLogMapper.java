package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.MediaLogResponseDTO;
import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.model.MediaLog;
import com.josec.catalog.model.ReadingLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

@Mapper(componentModel = "spring", uses = {ReadingLogMapper.class})
public interface MediaLogMapper {

    //Mapeo polimórfico
    @SubclassMapping(source = ReadingLog.class, target = ReadingLogResponseDTO.class)
    //TODO: añadir el resto de tipos de logs cuando estén hechos
    @Mapping(source = "user.username", target = "username")
    MediaLogResponseDTO toDTO(MediaLog mediaLog);
}
