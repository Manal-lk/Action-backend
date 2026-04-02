package com.xelops.actionplan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xelops.actionplan.enumeration.ActionStatusEnum;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActionDto {

    private Long id;

    @NotEmpty(message = "{" + GlobalConstants.ACTION_TITLE_REQUIRED_ERROR + "}")
    private String title;

    private String description;
    private String coverImageURL;
    private LocalDate startDate;
    private LocalDate dueDate;
    private ActionStatusEnum status;
    private Integer offset;

    @Min(value = 0, message = "{" + GlobalConstants.ACTION_ESTIMATION_NEGATIVE_ERROR + "}")
    @Max(value = 100, message = "{" + GlobalConstants.ACTION_ESTIMATION_EXCEEDS_MAX_VALUE_ERROR + "}")
    private Integer estimation;

    private boolean completed;

    private CustomFieldOptionSimplifiedDto priority;

    @Valid
    private UserSimplifiedDto assignee;

    @Valid
    private List<UserSimplifiedDto> members;
    private List<Long> memberTagIds;

    @Valid
    private List<CheckListDto> checkLists;

    @Valid
    private BoardColumnSimplifiedDto boardColumn;

    private UserSimplifiedDto actionOwner;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
