package com.xelops.actionplan.mapper;


import com.xelops.actionplan.domain.History;
import com.xelops.actionplan.dto.HistoryEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoryMapper {


    @Mapping(target = "action.id", source = "actionId")
    @Mapping(target = "actionHistoryType", source = "historyEventDto.actionHistoryType")
    History toHistory(HistoryEventDto historyEventDto, Long actionId);
}
