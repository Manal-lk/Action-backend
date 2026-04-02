package com.xelops.actionplan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrelloImportRequestDto {

    @NotBlank(message = "Board name is required")
    private String name;

    private String description;

    @Valid
    @NotNull(message = "Columns list is required")
    private List<TrelloColumnDto> columns;

    @Valid
    @NotNull(message = "Members list is required")
    private List<TrelloMemberDto> members;

    @Valid
    private List<CustomFieldDefinitionDto> customFields; // Optional custom fields with options
}
