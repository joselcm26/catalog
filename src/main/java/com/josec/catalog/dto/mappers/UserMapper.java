package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.UserProfileUpdateRequestDTO;
import com.josec.catalog.dto.UserRequestDTO;
import com.josec.catalog.dto.UserResponseDTO;
import com.josec.catalog.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para los usuarios
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Mapeo estándar de Entidad a DTO
     *
     * @param user entidad a mapear
     * @return UserResponseDTO
     */
    UserResponseDTO toDTO(User user);

    /**
     *  Mapeo a entidad
     *
     * @param userRequestDTO DTO a mapear
     * @return entidad User mapeada
     */
    User toEntity(UserRequestDTO userRequestDTO);

    /**
     * / Copia los datos del DTO a la entidad ignorando los nulos
     *
     * @param dto petición con los datos nuevos
     * @param entity entidad User a modificar
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserProfileUpdateRequestDTO dto, @MappingTarget User entity);

}
