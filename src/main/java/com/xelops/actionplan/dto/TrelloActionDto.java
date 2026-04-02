package com.xelops.actionplan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrelloActionDto {

    @NotBlank(message = "Action title is required")
    private String title;

    private String description;

    private LocalDate dueDate;

    @NotNull(message = "Action offset is required")
    private Integer offset;

    @Valid
    private TrelloMemberDto assignee;

    @Valid
    private List<TrelloMemberDto> members;

    @Valid
    private CustomFieldOptionSimplifiedDto priority;

    private Integer estimation;

    private List<Object> checkLists;
}
