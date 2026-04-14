package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.ReadingLogResponseDTO;
import com.josec.catalog.model.ReadingLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadingLogMapper {

    @Autowired
    private BookMapper bookMapper;

    /**
     * Mapeador a DTO
     *
     * @param readingLog a mapear
     * @return DTO de respuesta (incluye BookResposeDTO anidado)
     */
    public ReadingLogResponseDTO mapToDTO(ReadingLog readingLog) {
        ReadingLogResponseDTO dto = new ReadingLogResponseDTO();

        dto.setId(readingLog.getId());
        dto.setBook(bookMapper.mapToDTO(readingLog.getBook())); //Anidamiento de DTO
        dto.setReadDate(readingLog.getReadDate());
        dto.setRating(readingLog.getRating());
        dto.setPrivateComment(readingLog.getPrivateComment());
        dto.setCreatedAt(readingLog.getCreatedAt());

        return dto;
    }
}
