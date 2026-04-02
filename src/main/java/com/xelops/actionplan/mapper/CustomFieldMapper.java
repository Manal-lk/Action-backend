package com.xelops.actionplan.mapper;


import com.xelops.actionplan.domain.CustomFieldOption;
import com.xelops.actionplan.dto.CustomFieldOptionSimplifiedDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomFieldMapper {
    List<CustomFieldOptionSimplifiedDto> toCustomFieldOptionSimplifiedDto(List<CustomFieldOption> options);
}
