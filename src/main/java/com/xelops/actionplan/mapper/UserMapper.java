package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.UserDto;
import com.xelops.actionplan.dto.UserPrivilegesDto;
import com.xelops.actionplan.dto.UserSimplifiedDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    UserPrivilegesDto toUserPrivilegesDto(User user);

    List<UserDto> toUserDto(List<User> user);

    @Mapping(target = "name", source = "fullname")
    UserSimplifiedDto toUserSimplified(User boardUser);
}

