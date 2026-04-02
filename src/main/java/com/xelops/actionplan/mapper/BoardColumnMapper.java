package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.BoardColumn;
import com.xelops.actionplan.dto.BoardColumnCreationUpdateDto;
import com.xelops.actionplan.dto.BoardColumnDto;
import com.xelops.actionplan.dto.BoardColumnSimplifiedDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ActionMapper.class})
public interface BoardColumnMapper {
    @Mapping(target = "board.id", source = "boardId")
    BoardColumn toBoardColumn(BoardColumnCreationUpdateDto boardColumn);

    @Mapping(target = "name")
    @Mapping(target = "offset")
    @BeanMapping(ignoreByDefault = true)
    void toUpdatedBoardColumn(@MappingTarget BoardColumn existingBoardColumn, BoardColumn boardColumnUpdate);

    List<BoardColumnDto> toBoardColumnDtos(List<BoardColumn> boardColumn);

    List<BoardColumnSimplifiedDto> toBoardColumnSimplifiedDtos(List<BoardColumn> boardColumns);
}
