package com.xelops.actionplan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record UserInvitationDto(

        @NotEmpty(message = "Email list is required")
        List<@Email(message = "Email should be valid") String> emails,
        Long workspaceId,
        Long boardId
) {
}

