package com.xelops.actionplan.projections;

public interface ActionStatusBreakdownProjection {
    Double getCompletedRate();
    Long getCompletedCount();
    Double getOpenRate();
    Long getOpenCount();
}
