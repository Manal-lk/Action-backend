package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.UserProfileEnum;
import lombok.Builder;

@Builder
public record UserBoardDto(
        Long id,
        Long userId,
        UserProfileEnum profile,
        Boolean starred
) {}
