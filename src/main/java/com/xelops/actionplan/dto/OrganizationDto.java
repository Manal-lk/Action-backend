package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.OrganizationTypeEnum;
import lombok.Builder;

@Builder
public record OrganizationDto(
        Long id,
        String name,
        OrganizationTypeEnum type,
        String description,
        String realm,
        String logo,
        String primaryColor,
        String secondaryColor
) {
}

