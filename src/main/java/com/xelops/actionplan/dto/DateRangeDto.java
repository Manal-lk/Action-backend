package com.xelops.actionplan.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DateRangeDto(
    LocalDate startDate,
    LocalDate endDate
) {}
