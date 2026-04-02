package com.xelops.actionplan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record WorkspaceListingDto(
        Long id,
        String abbreviation,
        @NotNull(message = "Name is required")
        String name,
        String description,
        String imageUrl,
        int membersCount,
        int boardsCount,
        LocalDate updatedAt,
        List<UserWorkspaceDto> userWorkspaces
) {}