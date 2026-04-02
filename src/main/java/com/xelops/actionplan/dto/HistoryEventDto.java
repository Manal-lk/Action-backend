package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.ActionHistoryType;
import lombok.Builder;

@Builder
public record HistoryEventDto(
        Long oldConcernedUserId,
        Long concernedUserId,
        Long sourceColumnId,
        Long targetColumnId,
        String oldData,
        String newData,
        ActionHistoryType actionHistoryType
) {}
