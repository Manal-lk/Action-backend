package com.xelops.actionplan.dto;

import lombok.Builder;

@Builder

public record ActionStatisticsDto(
    Long total,
    Long completed,
    Long overdue,
    Double completionRate
) {}
