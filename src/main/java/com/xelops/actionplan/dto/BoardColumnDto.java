package com.xelops.actionplan.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BoardColumnDto(Long id, String name, Integer offset, List<ActionDto> actions) {
}
