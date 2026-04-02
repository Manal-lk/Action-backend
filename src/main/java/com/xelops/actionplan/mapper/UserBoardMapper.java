package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.UserBoard;
import com.xelops.actionplan.dto.UserBoardDto;
import com.xelops.actionplan.dto.UserProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserBoardMapper {
    @Mapping(target = "userId", source = "user.id")
    UserBoardDto toUserBoardDto(UserBoard userBoard);

    @Mapping(target = "profile", expression = "java(userBoard.getProfile().getLabel())")
    UserProfileDto userBoardToUserProfileDto(UserBoard userBoard);

    List<UserProfileDto> userBoardToUserProfileDto(List<UserBoard> userBoards);
}
