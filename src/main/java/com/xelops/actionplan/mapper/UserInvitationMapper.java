package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.Board;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.domain.UserInvitation;
import com.xelops.actionplan.domain.Workspace;
import com.xelops.actionplan.dto.UserInvitationDto;
import com.xelops.actionplan.dto.UserProfileDto;
import com.xelops.actionplan.enumeration.UserInvitationStatusEnum;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UserInvitationStatusEnum.class, UserProfileEnum.class, UUID.class})
public interface UserInvitationMapper {

    @Mapping(target = "hasPendingInvitation", constant = "true")
    @Mapping(target = "user.email", source = "email")
    @Mapping(target = "profile", expression = "java(userInvitation.getProfile().getLabel())")
    UserProfileDto userInvitationToUserProfileDto(UserInvitation userInvitation);
    List<UserProfileDto>  userInvitationToUserProfileDto(List<UserInvitation> userInvitations);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "workspace", expression = "java(workspace)")
    @Mapping(target = "board", expression = "java(board)")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "token", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "status", expression = "java(UserInvitationStatusEnum.PENDING)")
    @Mapping(target = "profile", expression = "java(UserProfileEnum.MEMBER)")
    @Mapping(target = "createdBy", expression = "java(user)")
    UserInvitation toUserInvitation(Workspace workspace, Board board, String email, User user);

    default List<UserInvitation> toUserInvitationList(UserInvitationDto userInvitationDto, @Context Workspace workspace, @Context Board board, @Context User user) {
        return userInvitationDto.emails()
                .stream()
                .map(email -> toUserInvitation(workspace, board, email, user))
                .toList();
    }
}

