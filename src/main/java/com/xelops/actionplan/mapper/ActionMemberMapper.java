package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.ActionMember;
import com.xelops.actionplan.dto.UserSimplifiedDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActionMemberMapper {

    @Mapping(target = "name", source = "member.fullname")
    @Mapping(target = "id", source = "member.id")
    UserSimplifiedDto toUserSimplifiedDto(ActionMember actionMember);
}
