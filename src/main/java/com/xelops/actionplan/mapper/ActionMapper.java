package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.Action;
import com.xelops.actionplan.dto.ActionDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ActionMemberMapper.class, CheckListMapper.class})
public interface ActionMapper {

    List<ActionDto> toActionDto(List<Action> actions);
    ActionDto toActionDto(Action action);
    Action toAction(ActionDto action);
}
