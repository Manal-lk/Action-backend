package com.xelops.actionplan.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilterRequestDto {
    private String search;
    private List<String> roles;
}