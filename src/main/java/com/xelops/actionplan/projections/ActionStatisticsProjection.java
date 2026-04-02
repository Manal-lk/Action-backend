package com.xelops.actionplan.projections;

public interface ActionStatisticsProjection {
    Long getTotal();
    Long getCompleted();
    Long getOverdue();
    Double getCompletionRate();
}
