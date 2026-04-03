package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.UserDto;
import com.xelops.actionplan.dto.UserFilterDto;
import com.xelops.actionplan.dto.UserPrivilegesDto;
import com.xelops.actionplan.dto.UserSimplifiedDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //  Mapper pour les privilèges
    @Mapping(target = "userId", source = "id")
    UserPrivilegesDto toUserPrivilegesDto(User user);

    //  Mapper liste complète
    List<UserDto> toUserDto(List<User> users);

    //  Mapper SIMPLIFIÉ (celui utilisé dans ton frontend)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "fullname")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    UserSimplifiedDto toUserSimplified(User user);

    // Mapper pour filtrage
    @Mapping(target = "appUserId", source = "id")
    @Mapping(target = "fullName", source = "fullname")
    @Mapping(target = "mail", source = "email")
    UserFilterDto toUserFilterDto(User user);

    //  Mapper liste filtrée
    List<UserFilterDto> toUserFilterDtoList(List<User> users);
}