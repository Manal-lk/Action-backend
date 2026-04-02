package com.xelops.actionplan.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ApiError {
    private String message;
    private String errCode;
}
