package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.UserRoleEnum;
import lombok.Builder;

@Builder
public record UserFilterDto(
        Long appUserId,
        String fullName,
        String mail,
        String username,
        UserRoleEnum role
) {}