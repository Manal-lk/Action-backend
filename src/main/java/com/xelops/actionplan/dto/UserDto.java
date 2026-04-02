package com.xelops.actionplan.dto;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String username,
        String email,
        String fullname
) {}
