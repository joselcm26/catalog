package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.ReadListResponseDTO;
import com.josec.catalog.model.ReadList;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {BookMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReadListMapper {

    ReadListResponseDTO toDTO(ReadList readList);

}
