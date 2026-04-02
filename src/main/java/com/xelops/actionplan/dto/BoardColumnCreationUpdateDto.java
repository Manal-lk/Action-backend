package com.xelops.actionplan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BoardColumnCreationUpdateDto(
    @NotBlank
    String name,
    @NotNull
    Integer offset,
    @NotNull
    Long boardId
) {}
