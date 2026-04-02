package com.xelops.actionplan.projections;

public interface ActionsTrendProjection {
    Integer getYear();
    Integer getMonth();
    Long getCreatedCount();
    Long getCompletedCount();
}
