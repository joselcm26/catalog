package com.josec.catalog.dto.mappers;

import com.josec.catalog.dto.CommentResponseDTO;
import com.josec.catalog.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author.id", target = "userId")
    @Mapping(source = "author.username", target = "username")
    @Mapping(source = "readingLog.id", target = "readingLogId")
    CommentResponseDTO toDTO(Comment comment);
}
