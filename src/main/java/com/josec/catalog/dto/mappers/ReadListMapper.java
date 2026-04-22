package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.ReadListResponseDTO;
import com.josec.catalog.model.ReadList;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface ReadListMapper {

    ReadListResponseDTO toDTO(ReadList readList);

}
