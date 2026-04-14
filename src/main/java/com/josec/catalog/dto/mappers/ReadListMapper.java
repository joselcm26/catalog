package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.ReadListResponseDTO;
import com.josec.catalog.model.ReadList;
import org.springframework.stereotype.Component;

@Component
public class ReadListMapper {

    public ReadListResponseDTO mapToDTO(ReadList readList) {
        ReadListResponseDTO dto = new ReadListResponseDTO();
        dto.setId(readList.getId());
        dto.setOwnerId(readList.getOwner().getId());
        dto.setBooks(readList.getBooks());

        return dto;
    }
}
