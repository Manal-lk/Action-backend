package com.xelops.actionplan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSimplifiedDto {

    @NotNull
    private Long id;
    private String name;
    private String username;
}

