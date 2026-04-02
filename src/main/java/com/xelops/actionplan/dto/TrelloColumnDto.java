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
public class TrelloColumnDto {

    @NotBlank(message = "Column name is required")
    private String name;

    @NotNull(message = "Column offset is required")
    private Integer offset;

    @Valid
    @NotNull(message = "Column actions list is required")
    private List<TrelloActionDto> actions;
}
