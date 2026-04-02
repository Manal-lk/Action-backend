package com.xelops.actionplan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WorkspaceDto(
        Long id,
        @NotNull(message = "Name is required")
        String name,
        String description,
        String imageUrl
) {}