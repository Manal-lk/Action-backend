package com.xelops.actionplan.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomFieldDefinitionDto {

    private String name;
    private String type;
    
    @Valid
    private List<CustomFieldOptionSimplifiedDto> options; // Options for list-type fields
}
