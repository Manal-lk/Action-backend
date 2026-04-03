package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.UserRoleEnum;
import lombok.Builder;

/**
 * DTO returned by POST /app-users/filter
 * Used for the User Management page.
 */
@Builder
public record UserManagementDto(
        Long id,
        String username,
        String email,
        String fullname,
        UserRoleEnum role
) {}