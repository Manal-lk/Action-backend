package com.xelops.actionplan.dto;

import lombok.Builder;

@Builder
public record OverdueActionsBreakdownDto(
    Long oneToThreeDays,
    Long fourToSevenDays,
    Long eightToFourteenDays,
    Long fifteenPlusDays
) {}
