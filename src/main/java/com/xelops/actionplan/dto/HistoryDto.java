package com.xelops.actionplan.dto;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record HistoryDto(
   String details,
   UserDto concernedUser,
   LocalDateTime timestamp
) {}
