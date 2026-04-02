package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.UserWorkspace;
import com.xelops.actionplan.dto.UserProfileDto;
import com.xelops.actionplan.dto.UserWorkspaceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserWorkspaceMapper {

    UserWorkspaceDto toUserWorkspaceDto(UserWorkspace userWorkspace);

    @Mapping(target = "profile", expression = "java(userWorkspace.getProfile().getLabel())")
    UserProfileDto userWorkspaceToUserProfileDto(UserWorkspace userWorkspace);

    List<UserProfileDto> userWorkspaceToUserProfileDto(List<UserWorkspace> userWorkspaces);

}
