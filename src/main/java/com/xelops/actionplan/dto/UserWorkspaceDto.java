package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.UserProfileEnum;
import lombok.Builder;

@Builder
public record UserWorkspaceDto(
        Long id,
        UserDto user,
        Long workspaceId,
        UserProfileEnum profile
) {}
