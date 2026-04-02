package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.CheckList;
import com.xelops.actionplan.domain.CheckListItem;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.CheckListDto;
import com.xelops.actionplan.dto.CheckListItemDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CheckListMapper {

    CheckListItemDto toCheckListItemDto(CheckListItem checkListItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", expression = "java(connectedUser)")
    CheckListItem toCheckListItem(CheckListItemDto checkListItemDto, @Context User connectedUser);

    CheckListDto toCheckListDto(CheckList checkList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "action.id", expression = "java(actionId)")
    @Mapping(target = "createdBy", expression = "java(connectedUser)")
    CheckList toCheckList(CheckListDto checkListDto, @Context User connectedUser, @Context Long actionId);

    List<CheckListDto> toCheckListDtoList(List<CheckList> checkLists);

    List<CheckList> toCheckList(List<CheckListDto> checkListDtoList, @Context User connectedUser, @Context Long actionId);

    List<CheckListItemDto> toCheckListItemDtoList(List<CheckListItem> checkListItems);

    @AfterMapping
    default void linkItems(@MappingTarget CheckList checkList) {
        if (checkList.getItems() != null) {
            checkList.getItems().forEach(item -> item.setCheckList(checkList));
        }
    }
}
