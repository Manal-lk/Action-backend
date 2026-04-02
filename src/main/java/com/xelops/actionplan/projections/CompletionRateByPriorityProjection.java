package com.xelops.actionplan.projections;

public interface CompletionRateByPriorityProjection {
    String getPriority();
    Double getCompletionRate();
    Long getTotal();
    Long getCompleted();
}
