package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.Board;
import com.xelops.actionplan.domain.BoardColumn;
import com.xelops.actionplan.dto.BoardCreationUpdateDto;
import com.xelops.actionplan.dto.BoardDto;
import com.xelops.actionplan.dto.BoardSimplifiedDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = { BoardColumnMapper.class, WorkspaceMapper.class, UserBoardMapper.class })
public interface BoardMapper {
    @Mapping(target = "name", source = "boardCreation.name")
    @Mapping(target = "description", source = "boardCreation.description")
    @Mapping(target = "visibility", source = "boardCreation.visibility")
    @Mapping(target = "active", source = "boardCreation.active")
    @Mapping(target = "workspace.id", source = "boardCreation.workspaceId")
    @Mapping(target = "createdBy.id", source = "userId")
    Board toBoard(BoardCreationUpdateDto boardCreation, Long userId);

    @Mapping(target = "name")
    @Mapping(target = "description")
    @Mapping(target = "visibility")
    @Mapping(target = "active")
    @Mapping(target = "backgroundImage")
    @Mapping(target = "workspace")
    @BeanMapping(ignoreByDefault = true)
    void toUpdatedBoard(@MappingTarget Board existingBoard, Board boardUpdate);

    @Mapping(target = "ownerId", source = "createdBy.id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "actionCount", source = "columns", qualifiedByName = "mapActionCount")
    BoardDto toBoardDto(Board board);

    BoardSimplifiedDto toBoardSimplifiedDto(Board board);

    @Named("mapActionCount")
    default Integer mapActionCount(List<BoardColumn> columns) {
        return columns.stream()
                .flatMap(column -> column.getActions().stream())
                .toList()
                .size();
    }
}
