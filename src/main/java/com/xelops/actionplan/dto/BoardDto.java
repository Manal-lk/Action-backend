package com.xelops.actionplan.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Builder
public record BoardDto(
        Long id,
        String name,
        String description,
        WorkspaceDto workspace,
        Long ownerId,
        LocalDate updatedAt,
        Integer actionCount,
        String backgroundImage,
        List<UserBoardDto> userBoards
) {
    // TODO: implement starring logic in the according US
    public Boolean isStarred() {
        return userBoards != null && userBoards.stream().anyMatch(userBoard -> Objects.equals(ownerId, userBoard.userId()) && Boolean.TRUE.equals(userBoard.starred()));
    }
}
