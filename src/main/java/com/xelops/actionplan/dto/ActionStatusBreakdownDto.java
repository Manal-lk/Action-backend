package com.xelops.actionplan.dto;

import lombok.Builder;

@Builder
public record ActionStatusBreakdownDto(
    Double completedRate,
    Long completedCount,
    Double openRate,
    Long openCount
) {}
