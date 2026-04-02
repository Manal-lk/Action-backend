package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.Comment;
import com.xelops.actionplan.dto.comment.CommentDto;
import com.xelops.actionplan.dto.comment.CreateCommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "action", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment createCommentDtoToComment(CreateCommentDto createCommentDto);

    CommentDto commentToCommentDto(Comment comment);

    List<CommentDto> commentsToCommentDtos(List<Comment> comments);
}
