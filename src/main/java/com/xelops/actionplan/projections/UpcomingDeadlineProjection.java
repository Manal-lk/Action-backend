package com.xelops.actionplan.projections;

public interface UpcomingDeadlineProjection {
    String getActionName();
    Long getDaysRemaining();
}
