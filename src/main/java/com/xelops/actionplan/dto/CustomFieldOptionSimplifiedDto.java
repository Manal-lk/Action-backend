package com.xelops.actionplan.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomFieldOptionSimplifiedDto {

    private Long id; // Optional - null means create new option
    private String label;
}
