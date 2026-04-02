package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.BoardVisibilityEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record BoardCreationUpdateDto(
    @NotBlank @Size(max = 100)
    String name,
    String description,
    BoardVisibilityEnum visibility,
    Boolean active,
    @NotNull
    Long workspaceId,
    List<UserBoardDto> userBoards
) {}
