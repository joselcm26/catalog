package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.model.MediaLog;
import com.josec.catalog.model.ReadingLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ReadingLogMapper.class})
public interface MediaLogMapper {

    // Este métödo es mágico: si le pasas un MediaLog que es un ReadingLog,
    // usará automáticamente el mapper de ReadingLog.
    default ReadingLogResponseDTO toDTO(MediaLog mediaLog) {
        if(mediaLog instanceof ReadingLog) {
            return toReadingLogDTO((ReadingLog) mediaLog);
        }
        // TODO: añadir los demás medios cuando se tengan
        return null;
    }
    ReadingLogResponseDTO toReadingLogDTO(ReadingLog readingLog);
}
