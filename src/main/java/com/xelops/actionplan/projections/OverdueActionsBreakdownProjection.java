package com.xelops.actionplan.projections;

public interface OverdueActionsBreakdownProjection {
    Long getOneToThreeDays();
    Long getFourToSevenDays();
    Long getEightToFourteenDays();
    Long getFifteenPlusDays();
}
