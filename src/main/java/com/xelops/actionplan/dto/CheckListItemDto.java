package com.xelops.actionplan.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record CheckListItemDto(
        Long id,
        @NotEmpty(message = "Checklist item description is required")
        String description,
        boolean checked
) {
}
