package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.DueIn;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ActionFilterDto(

    List<Long> assigneeIds,
    Long columnId,
    LocalDate startDueDate,
    LocalDate endDueDate,
    DueIn dueIn
) {}
