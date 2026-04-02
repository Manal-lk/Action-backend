package com.xelops.actionplan.dto;

import lombok.Builder;

@Builder
public record UserProfileDto(

        UserDto user,
        String profile,
        boolean hasPendingInvitation
) {
}
