package com.xelops.actionplan.dto;

public record CompletionRateByPriorityDto(String priority, Double completionRate, Long completed, Long total) {}
