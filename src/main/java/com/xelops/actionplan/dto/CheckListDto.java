package com.xelops.actionplan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record CheckListDto(
        Long id,
        @NotBlank(message = "Checklist title is required")
        String title,
        @Valid
        List<CheckListItemDto> items
) {
}
